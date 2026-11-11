package br.com.escolaDuartina.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.escolaDuartina.excecao.PdfLeituraException;

@RestController
@RequestMapping("/leitura-pdf")
@PreAuthorize("isAuthenticated()")
public class PdfLeituraController {
    private static final long MAX_BYTES = 25L * 1024 * 1024;
    private static final int MAX_PAGES = 1000;
    private static final int MAX_CHARACTERS = 5_000_000;

    public record Pagina(int numero, String texto) {}
    public record Documento(String nome, int totalPaginas, List<Pagina> paginas) {}

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Documento extrair(@RequestParam("arquivo") MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            throw new PdfLeituraException(HttpStatus.BAD_REQUEST, "Selecione um PDF com conteúdo.");
        }
        if (arquivo.getSize() > MAX_BYTES) {
            throw new PdfLeituraException(HttpStatus.PAYLOAD_TOO_LARGE, "O PDF deve ter até 25 MB.");
        }
        try {
            byte[] bytes = arquivo.getBytes();
            if (bytes.length < 5 || bytes[0] != '%' || bytes[1] != 'P' || bytes[2] != 'D'
                    || bytes[3] != 'F' || bytes[4] != '-') {
                throw new PdfLeituraException(HttpStatus.BAD_REQUEST, "O arquivo enviado não é um PDF válido.");
            }
            try (PDDocument document = Loader.loadPDF(bytes)) {
                if (!document.getCurrentAccessPermission().canExtractContent()) {
                    throw new PdfLeituraException(HttpStatus.BAD_REQUEST, "O PDF não permite extração de texto.");
                }
                if (document.getNumberOfPages() > MAX_PAGES) {
                    throw new PdfLeituraException(HttpStatus.BAD_REQUEST, "O limite é de 1.000 páginas por PDF.");
                }
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                List<Pagina> paginas = new ArrayList<>();
                int characters = 0;
                for (int numero = 1; numero <= document.getNumberOfPages(); numero++) {
                    stripper.setStartPage(numero);
                    stripper.setEndPage(numero);
                    String texto = stripper.getText(document).strip();
                    characters += texto.length();
                    if (characters > MAX_CHARACTERS) {
                        throw new PdfLeituraException(HttpStatus.BAD_REQUEST, "O PDF excede cinco milhões de caracteres.");
                    }
                    paginas.add(new Pagina(numero, texto));
                }
                if (paginas.stream().allMatch(pagina -> pagina.texto().isBlank())) {
                    throw new PdfLeituraException(HttpStatus.UNPROCESSABLE_ENTITY,
                            "Este PDF não contém texto selecionável. Execute OCR antes de enviá-lo.");
                }
                String nome = arquivo.getOriginalFilename();
                if (nome == null) nome = "Documento.pdf";
                nome = nome.replace('\\', '/');
                nome = nome.substring(nome.lastIndexOf('/') + 1);
                return new Documento(nome, document.getNumberOfPages(), paginas);
            }
        } catch (InvalidPasswordException exception) {
            throw new PdfLeituraException(HttpStatus.BAD_REQUEST, "Remova a senha do PDF antes de enviá-lo.");
        } catch (IOException exception) {
            throw new PdfLeituraException(HttpStatus.BAD_REQUEST, "Não foi possível ler o PDF. Verifique se está íntegro.");
        }
    }
}
