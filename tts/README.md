# Voz neural local

A aplicação usa Kokoro-82M v1.0 com pesos ONNX em precisão completa e conversão fonética Misaki configurada para `pt-br`. As vozes oferecidas são Dora, Alex e Santa, em português brasileiro. Dora é a seleção inicial. O Spring Boot inicia o worker Python no primeiro pedido e encerra o processo ao desligar. O worker atende em 127.0.0.1:8765 e exige um segredo gerado a cada inicialização. O navegador acessa a API Spring autenticada com JWT.

## Instalação no Windows

Na raiz do backend, execute `python -m venv .tts-venv` e `.\.tts-venv\Scripts\python.exe -m pip install -r tts/requirements.txt`.

Execute `python tts/download_kokoro.py` na raiz do backend para obter `kokoro-v1.0.onnx` e `voices-v1.0.bin`. O instalador retoma downloads interrompidos e verifica tamanho e SHA-256 contra os arquivos publicados pelo mantenedor. Os dois arquivos ocupam aproximadamente 354 MB.

Inicie o backend com `mvnw.cmd spring-boot:run` e o frontend com `npm start`. Entre na conta, abra Ouvir PDF e compare as amostras de Dora, Alex e Santa. O primeiro pedido carrega o modelo. Os seguintes reutilizam o modelo na memória. O player prepara o trecho seguinte durante a reprodução e libera os áudios anteriores. A síntese usa velocidade 0,98 para uma cadência levemente mais lenta. O controle do player permite ajustar a reprodução preservando o tom.

Os modelos e o ambiente Python ficam fora do Git. Em uma nova máquina, repita a instalação. PDFs digitalizados precisam de OCR. O conteúdo é lido no idioma em que foi escrito. Os IDs anteriores do Piper continuam aceitos pela API se seus modelos estiverem instalados.

## Fontes e licenças

- Kokoro-82M, Apache-2.0, https://huggingface.co/hexgrad/Kokoro-82M
- Vozes brasileiras, https://huggingface.co/hexgrad/Kokoro-82M/blob/main/VOICES.md
- kokoro-onnx, MIT, https://github.com/thewh1teagle/kokoro-onnx
- Motor Piper para IDs anteriores, GPL-3.0, https://github.com/OHF-Voice/piper1-gpl
- Faber, dataset CC0, https://huggingface.co/rhasspy/piper-voices/blob/main/pt/pt_BR/faber/medium/MODEL_CARD
- Tugão, dataset CC0, https://huggingface.co/rhasspy/piper-voices/blob/main/pt/pt_PT/tug%C3%A3o/medium/MODEL_CARD

Consulte os termos completos dos componentes ao redistribuir a aplicação e seus modelos.
