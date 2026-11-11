import io
import os
import unittest
import wave
from unittest.mock import patch

import numpy as np

os.environ.setdefault("PIPER_WORKER_TOKEN", "unit-test")
import server


class BrazilianVoiceTest(unittest.TestCase):
    def test_onnx_speed_remains_floating_point(self):
        model = server.BrazilianKokoro.__new__(server.BrazilianKokoro)
        with patch.object(model, "tokenizer", create=True) as tokenizer, patch.object(model, "sess", create=True) as session:
            tokenizer.tokenize.return_value = [1, 2]
            session.run.return_value = [np.zeros(10, dtype=np.float32)]
            model._create_audio("fonemas", np.zeros((3, 256), dtype=np.float32), 0.98)
            inputs = session.run.call_args.args[1]
            self.assertEqual(inputs["speed"].dtype, np.dtype("float32"))
            self.assertAlmostEqual(float(inputs["speed"][0]), 0.98, places=5)
            self.assertEqual(inputs["input_ids"].dtype, np.dtype("int64"))

    def test_brazilian_phonemes_and_voice_are_used(self):
        with patch.object(server, "kokoro", None), patch.object(server, "portuguese", None):
            with patch.object(server, "BrazilianKokoro") as model, patch.object(server, "EspeakG2P") as g2p:
                g2p.return_value.return_value = "fonemas"
                model.return_value.create.return_value = (np.array([0.2, -0.2], dtype=np.float32), 24000)
                result = server.synthesize("Uma frase em português brasileiro.", "br-dora")
                g2p.assert_called_once_with(language="pt-br")
                model.return_value.create.assert_called_once_with("fonemas", "pf_dora", speed=0.98, is_phonemes=True)
                with wave.open(io.BytesIO(result), "rb") as audio:
                    self.assertEqual(audio.getframerate(), 24000)
                    self.assertEqual(audio.getnchannels(), 1)
                    self.assertEqual(audio.getnframes(), 2)

    def test_empty_phonemes_are_rejected(self):
        with patch.object(server, "kokoro") as model, patch.object(server, "portuguese") as g2p:
            g2p.return_value = ""
            with self.assertRaises(ValueError):
                server.synthesize("...", "br-alex")
            model.create.assert_not_called()


if __name__ == "__main__":
    unittest.main()
