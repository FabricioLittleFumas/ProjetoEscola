"""Private neural speech worker owned by Spring Boot."""
import io
import json
import os
import threading
import wave
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path

import numpy as np
from kokoro_onnx import Kokoro
from misaki.espeak import EspeakG2P

MODELS = Path(__file__).parent / "models"
VOICES = {"br-faber": "pt_BR-faber-medium", "pt-tugao": "pt_PT-tugao-medium"}
BRAZILIAN_VOICES = {"br-dora": "pf_dora", "br-alex": "pm_alex", "br-santa": "pm_santa"}
TOKEN = os.environ["PIPER_WORKER_TOKEN"]
PORT = int(os.environ.get("PIPER_WORKER_PORT", "8765"))
voices = {}
lock = threading.Lock()
kokoro = None
portuguese = None


class BrazilianKokoro(Kokoro):
    def _create_audio(self, phonemes, voice, speed):
        # kokoro-onnx 0.4.7 sends integer speed for this ONNX export.
        # The published v1.0 graph requires floating point speed.
        tokens = self.tokenizer.tokenize(phonemes)
        inputs = {
            "input_ids": np.array([[0, *tokens, 0]], dtype=np.int64),
            "style": np.array(voice[len(tokens)], dtype=np.float32),
            "speed": np.array([speed], dtype=np.float32),
        }
        return self.sess.run(None, inputs)[0], 24000


def synthesize(text, voice_id):
    global kokoro, portuguese
    output = io.BytesIO()
    if voice_id in BRAZILIAN_VOICES:
        if kokoro is None:
            # Full precision weights preserve the source model's output quality.
            kokoro = BrazilianKokoro(str(MODELS / "kokoro-v1.0.onnx"), str(MODELS / "voices-v1.0.bin"))
            portuguese = EspeakG2P(language="pt-br")
        phonemes = portuguese(text)
        if not phonemes.strip():
            raise ValueError("No pronounceable text")
        samples, rate = kokoro.create(phonemes, BRAZILIAN_VOICES[voice_id],
                                      speed=0.98, is_phonemes=True)
        pcm = (np.clip(samples, -1.0, 1.0) * 32767).astype("<i2")
        with wave.open(output, "wb") as wav:
            wav.setnchannels(1)
            wav.setsampwidth(2)
            wav.setframerate(rate)
            wav.writeframes(pcm.tobytes())
    else:
        # Retain support for voice IDs used by existing clients.
        from piper import PiperVoice, SynthesisConfig
        name = VOICES[voice_id]
        if name not in voices:
            voices[name] = PiperVoice.load(str(MODELS / (name + ".onnx")))
        with wave.open(output, "wb") as wav:
            voices[name].synthesize_wav(text, wav, syn_config=SynthesisConfig(length_scale=1.0))
    return output.getvalue()


class Handler(BaseHTTPRequestHandler):
    def log_message(self, *_args):
        pass

    def do_GET(self):
        if self.headers.get("Authorization") != TOKEN:
            self.send_error(403)
            return
        self.send_response(200)
        self.end_headers()
        self.wfile.write(b"ready")

    def do_POST(self):
        if self.headers.get("Authorization") != TOKEN:
            self.send_error(403)
            return
        length = int(self.headers.get("Content-Length", "0"))
        if length <= 0 or length > 16000:
            self.send_error(400)
            return
        if not lock.acquire(blocking=False):
            self.send_error(429)
            return
        try:
            data = json.loads(self.rfile.read(length))
            voice_id = data["voz"]
            if voice_id not in BRAZILIAN_VOICES and voice_id not in VOICES:
                self.send_error(400)
                return
            text = data["texto"].strip()
            if not text or len(text) > 1500:
                self.send_error(400)
                return
            audio = synthesize(text, voice_id)
            self.send_response(200)
            self.send_header("Content-Type", "audio/wav")
            self.send_header("Content-Length", str(len(audio)))
            self.end_headers()
            self.wfile.write(audio)
        except (ValueError, KeyError):
            self.send_error(400)
        except Exception:
            self.send_error(503, "Neural synthesis unavailable")
        finally:
            lock.release()


if __name__ == "__main__":
    ThreadingHTTPServer(("127.0.0.1", PORT), Handler).serve_forever()
