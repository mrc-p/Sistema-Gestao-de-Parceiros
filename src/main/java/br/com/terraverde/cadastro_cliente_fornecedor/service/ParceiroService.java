package br.com.terraverde.cadastro_cliente_fornecedor.service;

import br.com.terraverde.cadastro_cliente_fornecedor.model.Parceiro;
import br.com.terraverde.cadastro_cliente_fornecedor.repository.ParceiroRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ParceiroService {

    @Autowired
    private ParceiroRepository parceiroRepository;

    /**
     * Gera um relatório PDF completo de todos os parceiros com resumos.
     * @return Array de bytes do documento PDF.
     */
    public byte[] gerarRelatorioPdf() throws DocumentException {
        // 1. Coleta de dados
        List<Parceiro> parceiros = parceiroRepository.findAll();
        
        // Dados de resumo estatístico
        long totalClientes = parceiroRepository.countByTipo("CLIENTE");
        long totalFornecedores = parceiroRepository.countByTipo("FORNECEDOR");
        long totalParceiros = parceiros.size(); // Ou totalClientes + totalFornecedores
        
        // Novos dados de resumo (Top 10 e Último Mês)
        List<Parceiro> top10Parceiros = parceiroRepository.findTop10ByOrderByDataHoraCadastroDesc();
        LocalDateTime umMesAtras = LocalDateTime.now().minusDays(30);
        List<Parceiro> parceirosUltimoMes = parceiroRepository.findByDataHoraCadastroAfter(umMesAtras);
        
        // 2. Configuração do Documento PDF
        Document document = new Document(PageSize.A4.rotate()); 
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Configuração de Estilos
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK);
            Font sectionTitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
            Font dataSummaryFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.WHITE);
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Título Principal e Data
            document.add(new Paragraph("RELATÓRIO COMPLETO DE PARCEIROS (CLIENTES E FORNECEDORES)", titleFont));
            document.add(new Paragraph("Data de Geração: " + LocalDateTime.now().format(formatter) + "\n"));
            
            // --- BLOCO NOVO: RESUMO ESTATÍSTICO ---
            document.add(new Paragraph("Total de Parceiros Cadastrados: " + totalParceiros, dataSummaryFont));
            document.add(new Paragraph("Total de Clientes: " + totalClientes, dataSummaryFont));
            document.add(new Paragraph("Total de Fornecedores: " + totalFornecedores + "\n\n", dataSummaryFont));
            // --- FIM BLOCO NOVO ---
            
            // ----------------------------------------------------------------------
            // --- 1. TABELA PRINCIPAL (Relatório Completo) ---
            // ----------------------------------------------------------------------
            
            document.add(new Paragraph("1. Relatório Completo", sectionTitleFont));
            PdfPTable table = new PdfPTable(8); 
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            
            float[] widths = {0.5f, 2f, 1f, 1.5f, 2.5f, 1f, 1f, 2f}; 
            table.setWidths(widths);

            // Cabeçalhos da Tabela Principal
            addTableHeader(table, headerFont, "ID");
            addTableHeader(table, headerFont, "NOME/RAZÃO SOCIAL");
            addTableHeader(table, headerFont, "DOCUMENTO");
            addTableHeader(table, headerFont, "TIPO"); 
            addTableHeader(table, headerFont, "EMAIL");
            addTableHeader(table, headerFont, "TELEFONE");
            addTableHeader(table, headerFont, "CADASTRO");
            addTableHeader(table, headerFont, "OBSERVAÇÕES");

            // Preenchimento da Tabela Principal
            for (Parceiro parceiro : parceiros) {
                String dataCadastro = parceiro.getDataHoraCadastro() != null 
                    ? parceiro.getDataHoraCadastro().format(formatter) : "";

                table.addCell(createCell(parceiro.getId().toString(), dataFont, Element.ALIGN_CENTER));
                table.addCell(createCell(parceiro.getNomeOuRazaoSocial(), dataFont, Element.ALIGN_LEFT));
                table.addCell(createCell(parceiro.getDocumento(), dataFont, Element.ALIGN_CENTER));
                table.addCell(createCell(parceiro.getTipo(), dataFont, Element.ALIGN_CENTER)); 
                table.addCell(createCell(parceiro.getEmail(), dataFont, Element.ALIGN_LEFT));
                table.addCell(createCell(parceiro.getTelefone(), dataFont, Element.ALIGN_CENTER));
                table.addCell(createCell(dataCadastro, dataFont, Element.ALIGN_CENTER));
                table.addCell(createCell(parceiro.getObservacoes(), dataFont, Element.ALIGN_LEFT));
            }

            document.add(table);
            document.add(new Paragraph("\n\n")); 
            
            // ----------------------------------------------------------------------
            // --- 2. TABELA DOS 10 ÚLTIMOS CADASTROS ---
            // ----------------------------------------------------------------------
            
            document.add(new Paragraph("2. Resumo: 10 Últimos Parceiros Cadastrados", sectionTitleFont));
            float[] summaryWidths = {0.5f, 3f, 1.5f, 1f, 1.5f}; 
            document.add(createSummaryTable(top10Parceiros, headerFont, dataFont, formatter, summaryWidths));

            document.add(new Paragraph("\n\n")); 
            
            // ----------------------------------------------------------------------
            // --- 3. TABELA DE CADASTROS NO ÚLTIMO MÊS ---
            // ----------------------------------------------------------------------
            
            document.add(new Paragraph("3. Resumo: Parceiros Cadastrados no Último Mês (" 
                + umMesAtras.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - Hoje)", 
                sectionTitleFont));
            document.add(createSummaryTable(parceirosUltimoMes, headerFont, dataFont, formatter, summaryWidths));
            
            document.close();
            
            return baos.toByteArray();

        } catch (DocumentException e) {
            throw new DocumentException("Erro ao gerar PDF: " + e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
    
    /** * Método auxiliar que cria uma tabela de resumo (5 colunas). */
    private PdfPTable createSummaryTable(List<Parceiro> parceiros, Font headerFont, Font dataFont, DateTimeFormatter formatter, float[] widths) throws DocumentException {
        PdfPTable table = new PdfPTable(5); // 5 Colunas: ID, Nome, Documento, Tipo, Cadastro
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(widths);

        // Cabeçalhos
        addTableHeader(table, headerFont, "ID");
        addTableHeader(table, headerFont, "NOME/RAZÃO SOCIAL");
        addTableHeader(table, headerFont, "DOCUMENTO");
        addTableHeader(table, headerFont, "TIPO");
        addTableHeader(table, headerFont, "CADASTRO");

        // Dados
        for (Parceiro parceiro : parceiros) {
            String dataCadastro = parceiro.getDataHoraCadastro() != null 
                ? parceiro.getDataHoraCadastro().format(formatter) : "";

            table.addCell(createCell(parceiro.getId().toString(), dataFont, Element.ALIGN_CENTER));
            table.addCell(createCell(parceiro.getNomeOuRazaoSocial(), dataFont, Element.ALIGN_LEFT));
            table.addCell(createCell(parceiro.getDocumento(), dataFont, Element.ALIGN_CENTER));
            table.addCell(createCell(parceiro.getTipo(), dataFont, Element.ALIGN_CENTER)); 
            table.addCell(createCell(dataCadastro, dataFont, Element.ALIGN_CENTER));
        }
        return table;
    }
    
    /** Cria célula de cabeçalho. */
    private void addTableHeader(PdfPTable table, Font headerFont, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new BaseColor(52, 73, 94)); 
        cell.setPadding(5);
        cell.setBorderWidth(0.5f);
        table.addCell(cell);
    }
    
    /** Cria célula padrão para dados. */
    private PdfPCell createCell(String content, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4);
        cell.setBorderWidth(0.5f);
        return cell;
    }
}