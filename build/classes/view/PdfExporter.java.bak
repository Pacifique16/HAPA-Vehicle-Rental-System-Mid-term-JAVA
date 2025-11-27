package view;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFont;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PdfExporter {
    
    public static void exportTableToPdf(JTable table, String title, String filePath) throws Exception {
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        PdfFont font = PdfFontFactory.createFont();
        
        // Title
        Paragraph titlePara = new Paragraph(title)
                .setFont(font)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold();
        document.add(titlePara);
        
        // Date
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Paragraph datePara = new Paragraph("Generated on: " + dateTime)
                .setFont(font)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(datePara);
        
        document.add(new Paragraph("\n"));
        
        // Table
        TableModel model = table.getModel();
        int columnCount = model.getColumnCount();
        
        Table pdfTable = new Table(columnCount);
        
        // Headers
        for (int i = 0; i < columnCount; i++) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(model.getColumnName(i)))
                    .setFont(font)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            pdfTable.addHeaderCell(headerCell);
        }
        
        // Data rows
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < columnCount; col++) {
                Object value = model.getValueAt(row, col);
                String cellValue = value != null ? value.toString() : "";
                
                Cell dataCell = new Cell()
                        .add(new Paragraph(cellValue))
                        .setFont(font)
                        .setTextAlignment(TextAlignment.LEFT);
                pdfTable.addCell(dataCell);
            }
        }
        
        document.add(pdfTable);
        document.close();
    }
}