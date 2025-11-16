package br.com.terraverde.cadastro_cliente_fornecedor.controller;

import br.com.terraverde.cadastro_cliente_fornecedor.service.ParceiroService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para lidar com relatórios.
 */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioRestController {

    @Autowired
    private ParceiroService parceiroService;

    /**
     * Endpoint para gerar o relatório completo de Parceiros em formato PDF.
     * Mapeado para GET /api/relatorios/parceiros-pdf
     * @return O arquivo PDF como um array de bytes.
     */
    @GetMapping("/parceiros-pdf")
    public ResponseEntity<byte[]> gerarRelatorioParceirosPdf() {
        try {
            byte[] pdfBytes = parceiroService.gerarRelatorioPdf();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "relatorio_parceiros_completo.pdf";
            // Define o nome do arquivo para download no navegador
            headers.setContentDispositionFormData("attachment", filename); 
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (DocumentException e) {
            // Log do erro e retorno de um ResponseEntity adequado
            System.err.println("Erro ao gerar o PDF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Erro ao gerar o PDF: " + e.getMessage()).getBytes());
        }
    }
}