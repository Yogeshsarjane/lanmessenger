package com.lanmessenger.service;

import com.lanmessenger.model.Fault;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfGenerationService {

    public ByteArrayInputStream generateFaultsPdf(List<Fault> faults) {
        Document document = new Document(PageSize.A4.rotate()); // Use landscape for more space
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- Add Title ---
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Live Fault Reports", fontTitle);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Add a little space

            // --- Create Table with 5 columns ---
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2, 5, 2, 2}); // Relative column widths

            // --- Create Table Header ---
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            String[] headers = {"Date / Time", "Reporter", "Subject", "Priority", "Category"};
            for (String header : headers) {
                PdfPCell hcell = new PdfPCell(new Phrase(header, headFont));
                hcell.setBackgroundColor(Color.LIGHT_GRAY);
                hcell.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
                hcell.setPadding(5);
                table.addCell(hcell);
            }

            // --- Add Data Rows ---
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Fault fault : faults) {
                table.addCell(fault.getSubmissionTimestamp().format(formatter));
                table.addCell(fault.getReporterUsername());
                table.addCell(fault.getSubject());
                table.addCell(fault.getPriority().name());
                table.addCell(fault.getCategory().name());
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            // In a real application, you'd handle this error more gracefully
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}