package br.com.escolaDuartina.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import br.com.escolaDuartina.excecao.PdfLeituraException;

class PdfLeituraControllerTest {
    private final PdfLeituraController controller = new PdfLeituraController();

    @Test
    void extractsAllPagesInOrder() throws Exception {
        var response = controller.extrair(pdf(true));
        assertEquals(2, response.totalPaginas());
        assertEquals(1, response.paginas().get(0).numero());
        assertTrue(response.paginas().get(0).texto().contains("Primeira pagina"));
        assertTrue(response.paginas().get(1).texto().contains("Segunda pagina"));
    }

    @Test
    void rejectsRenamedNonPdf() {
        var file = new MockMultipartFile("arquivo", "falso.pdf", "application/pdf", "texto".getBytes());
        var error = assertThrows(PdfLeituraException.class, () -> controller.extrair(file));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
    }

    @Test
    void reportsDocumentsRequiringOcr() throws Exception {
        var file = pdf(false);
        var error = assertThrows(PdfLeituraException.class, () -> controller.extrair(file));
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, error.getStatus());
    }

    private MockMultipartFile pdf(boolean withText) throws Exception {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            for (String text : new String[] { "Primeira pagina", "Segunda pagina" }) {
                PDPage page = new PDPage();
                document.addPage(page);
                if (withText) {
                    try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                        content.beginText();
                        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                        content.newLineAtOffset(40, 700);
                        content.showText(text);
                        content.endText();
                    }
                }
            }
            document.save(output);
            return new MockMultipartFile("arquivo", "leitura.pdf", "application/pdf", output.toByteArray());
        }
    }
}
