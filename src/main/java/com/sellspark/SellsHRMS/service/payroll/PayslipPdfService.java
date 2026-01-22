package com.sellspark.SellsHRMS.service.payroll;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipDTO;
import com.sellspark.SellsHRMS.entity.Employee;
import com.sellspark.SellsHRMS.exception.EmployeeNotFoundException;
import com.sellspark.SellsHRMS.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

import com.sellspark.SellsHRMS.dto.payroll.SalarySlipComponentDTO;
import org.springframework.stereotype.Service;
import com.lowagie.text.Document; 
import com.lowagie.text.PageSize; 
import com.lowagie.text.Paragraph; 
import com.lowagie.text.Phrase; 
import com.lowagie.text.Font; 
import com.lowagie.text.FontFactory; 
import com.lowagie.text.Chunk; 
import com.lowagie.text.Rectangle; 
import com.lowagie.text.Element; 
import com.lowagie.text.pdf.PdfWriter; 
import com.lowagie.text.pdf.PdfPTable; 
import com.lowagie.text.pdf.PdfPCell; 
import com.lowagie.text.pdf.ColumnText; 
import com.lowagie.text.pdf.PdfPageEventHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.awt.Color;


@Service
@RequiredArgsConstructor
public class PayslipPdfService {

    private final EmployeeRepository employeeRepository;

    public ByteArrayInputStream generatePayslipPDF(SalarySlipDTO slip) {
        
        Employee emp = employeeRepository.findById(slip.getEmployeeId())
        .orElseThrow(() ->  new EmployeeNotFoundException(slip.getEmployeeId()));
        
        Document document = new Document(PageSize.A4, 36, 36, 72, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = PdfWriter.getInstance(document, out);
        writer.setPageEvent(new PayslipWatermarkEvent("SellsHRMS"));

        document.open();

        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
        Paragraph title = new Paragraph("PAYSLIP", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Employee Info
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        PdfPTable empTable = new PdfPTable(2);
        empTable.setWidthPercentage(100);
        empTable.addCell(cell("Employee Name:", emp.getFirstName() + " " + emp.getLastName(), infoFont));
        empTable.addCell(cell("Employee ID:", String.valueOf(emp.getEmployeeCode()), infoFont));
        empTable.addCell(cell("Pay Period:", slip.getFromDate() + " → " + slip.getToDate(), infoFont));
        empTable.addCell(cell("Pay Run:", String.valueOf(slip.getPayRunId()), infoFont));
        document.add(empTable);
        document.add(Chunk.NEWLINE);

        // Earnings / Deductions Table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell(headerCell("Earnings"));
        table.addCell(headerCell("Deductions"));

        List<SalarySlipComponentDTO> earnings = slip.getComponents().stream()
                .filter(c -> "EARNING".equalsIgnoreCase(c.getComponentType())).toList();
        List<SalarySlipComponentDTO> deductions = slip.getComponents().stream()
                .filter(c -> "DEDUCTION".equalsIgnoreCase(c.getComponentType())).toList();

        int maxRows = Math.max(earnings.size(), deductions.size());
        for (int i = 0; i < maxRows; i++) {
            SalarySlipComponentDTO e = i < earnings.size() ? earnings.get(i) : null;
            SalarySlipComponentDTO d = i < deductions.size() ? deductions.get(i) : null;
            table.addCell(detailCell(e != null ? e.getComponentName() + " - ₹" + e.getAmount() : ""));
            table.addCell(detailCell(d != null ? d.getComponentName() + " - ₹" + d.getAmount() : ""));
        }

        table.addCell(totalCell("Gross Pay: ₹" + slip.getGrossPay()));
        table.addCell(totalCell("Total Deductions: ₹" + slip.getTotalDeductions()));
        document.add(table);

        // Net Pay Summary
        document.add(Chunk.NEWLINE);
        Paragraph netPay = new Paragraph("Net Pay: ₹" + slip.getNetPay(),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLUE));
        netPay.setAlignment(Element.ALIGN_RIGHT);
        document.add(netPay);

        document.add(Chunk.NEWLINE);
        Paragraph words = new Paragraph("In words: " + toWords(Math.round(slip.getNetPay())) + " only",
                FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY));
        document.add(words);

        // Signature
        document.add(Chunk.NEWLINE);
        Paragraph sign = new Paragraph("Authorized Signatory",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9));
        sign.setAlignment(Element.ALIGN_RIGHT);
        document.add(sign);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private PdfPCell cell(String label, String value, Font font) {
        Phrase phrase = new Phrase(label + " " + value, font);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new Color(240, 240, 240));
        return cell;
    }

    private PdfPCell detailCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell totalCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        cell.setBackgroundColor(new Color(250, 250, 250));
        return cell;
    }

    private String toWords(long n) {
        // Simple converter for Indian format
        String[] units = { "", "Thousand", "Lakh", "Crore" };
        if (n == 0) return "Zero Rupees";
        StringBuilder sb = new StringBuilder();
        int unitIndex = 0;
        while (n > 0 && unitIndex < units.length) {
            int part = (int) (n % 1000);
            if (part > 0) sb.insert(0, part + " " + units[unitIndex] + " ");
            n /= 1000;
            unitIndex++;
        }
        return sb.toString().trim() + " Rupees";
    }

    // ───────────────────────────────────────────────
    // Inner class for watermark
    // ───────────────────────────────────────────────
    private static class PayslipWatermarkEvent extends PdfPageEventHelper {
        private final String watermark;
        public PayslipWatermarkEvent(String watermark) { this.watermark = watermark; }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(writer.getDirectContentUnder(),
                    Element.ALIGN_CENTER,
                    new Phrase(watermark, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 40, Color.LIGHT_GRAY)),
                    297.5f, 421, writer.getPageNumber() % 2 == 1 ? 45 : -45);
        }
    }
}
