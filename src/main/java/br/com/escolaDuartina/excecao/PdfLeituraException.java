package br.com.escolaDuartina.excecao;

import org.springframework.http.HttpStatus;

public class PdfLeituraException extends RuntimeException {
    private final HttpStatus status;

    public PdfLeituraException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
