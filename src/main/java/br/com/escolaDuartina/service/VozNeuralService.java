package br.com.escolaDuartina.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import br.com.escolaDuartina.excecao.PdfLeituraException;

@Service
public class VozNeuralService {
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String token = UUID.randomUUID().toString();
    private Process worker;

    private synchronized void iniciar() throws IOException, InterruptedException {
        if (worker != null && worker.isAlive()) return;
        boolean windows = System.getProperty("os.name").toLowerCase().contains("win");
        Path python = Path.of(".tts-venv", windows ? "Scripts/python.exe" : "bin/python").toAbsolutePath();
        ProcessBuilder builder = new ProcessBuilder(python.toString(), "tts/server.py");
        builder.environment().put("PIPER_WORKER_TOKEN", token);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        worker = builder.start();
        for (int tentativa = 0; tentativa < 40; tentativa++) {
            try {
                var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:8765/"))
                        .header("Authorization", token).timeout(Duration.ofSeconds(1)).GET().build();
                if (client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode() == 200) return;
            } catch (IOException unavailable) {
                if (!worker.isAlive()) break;
            }
            Thread.sleep(250);
        }
        worker.destroyForcibly();
        throw new IOException("Neural worker did not start");
    }

    public byte[] gerar(String texto, String voz) {
        try {
            iniciar();
            String json = mapper.writeValueAsString(java.util.Map.of("texto", texto, "voz", voz));
            var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:8765/"))
                    .header("Authorization", token).header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(90)).POST(HttpRequest.BodyPublishers.ofString(json)).build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 429) {
                throw new PdfLeituraException(HttpStatus.TOO_MANY_REQUESTS, "A voz está processando outra leitura. Tente novamente em instantes.");
            }
            if (response.statusCode() != 200) throw new IOException("Synthesis failed");
            return response.body();
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw indisponivel();
        } catch (IOException unavailable) {
            throw indisponivel();
        }
    }

    private PdfLeituraException indisponivel() {
        return new PdfLeituraException(HttpStatus.SERVICE_UNAVAILABLE,
                "A voz neural está indisponível. Verifique o ambiente Python e os modelos Kokoro em tts/models.");
    }

    @PreDestroy
    public synchronized void encerrar() {
        if (worker != null) {
            worker.descendants().forEach(ProcessHandle::destroy);
            worker.destroy();
        }
    }
}
