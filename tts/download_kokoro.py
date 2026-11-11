"""Download and verify the exact Kokoro model files used by this application."""
import hashlib
import time
import urllib.error
import urllib.request
from pathlib import Path

BASE = "https://github.com/thewh1teagle/kokoro-onnx/releases/download/model-files-v1.1/"
FILES = {
    "kokoro-v1.0.onnx": (325505369, "beb0d1848dee9a49da392cc3df26958d46cfa35d321edf434f52949153f0df3a"),
    "voices-v1.0.bin": (28214398, "bca610b8308e8d99f32e6fe4197e7ec01679264efed0cac9140fe9c29f1fbf7d"),
}


def download(name, size, digest):
    target = Path(__file__).parent / "models" / name
    target.parent.mkdir(parents=True, exist_ok=True)
    for attempt in range(12):
        offset = target.stat().st_size if target.exists() else 0
        if offset >= size:
            break
        request = urllib.request.Request(BASE + name, headers={"Range": f"bytes={offset}-"})
        try:
            with urllib.request.urlopen(request, timeout=45) as response:
                if offset and (response.status != 206 or not response.headers.get("Content-Range", "").startswith(f"bytes {offset}-")):
                    raise ValueError("Server did not honor the resume offset")
                with target.open("ab") as output:
                    while block := response.read(256 * 1024):
                        output.write(block)
        except (OSError, urllib.error.URLError) as error:
            print(f"Retry {attempt + 1}: {name} ({type(error).__name__})", flush=True)
            time.sleep(2)
    with target.open("rb") as model:
        actual = hashlib.file_digest(model, "sha256").hexdigest()
    if target.stat().st_size != size or actual != digest:
        raise ValueError(f"Incomplete or invalid model: {target}. Run the downloader again to resume an incomplete file.")
    print(f"Verified SHA-256: {name}", flush=True)


if __name__ == "__main__":
    for name, (size, digest) in FILES.items():
        download(name, size, digest)
