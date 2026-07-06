package com.telecom.invoicePdfGeneration.services.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.telecom.invoicePdfGeneration.dto.internal.InvoiceDetailsDto;
import com.telecom.invoicePdfGeneration.services.PdfGenerationService;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGenerationServiceImpl implements PdfGenerationService {

    // These could later be moved to properties or config
    private static final String COMPANY_NAME = "Telecom Enterprise BSS";
    private static final String FOOTER_MESSAGE = "Thank you for your business. For any queries, please contact support.";

    @Override
    public byte[] generateInvoicePdf(InvoiceDetailsDto invoiceDetails) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            addHeader(document);
            addCustomerInformation(document, invoiceDetails);
            addInvoiceDetails(document, invoiceDetails);
            addChargesTable(document, invoiceDetails);
            addFooter(document);
            
            document.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while generating PDF", e);
        }
    }

    private void addHeader(Document document) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
        Paragraph header = new Paragraph(COMPANY_NAME, headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(20);
        document.add(header);
        
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.DARK_GRAY);
        Paragraph title = new Paragraph("INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
    }

    private void addCustomerInformation(Document document, InvoiceDetailsDto details) {
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        
        Paragraph customerInfo = new Paragraph();
        customerInfo.add(new Phrase("Customer Name: ", boldFont));
        customerInfo.add(new Phrase((details.getFirstName() != null ? details.getFirstName() : "") + " " + 
                                    (details.getLastName() != null ? details.getLastName() : "") + "\n", normalFont));
        
        customerInfo.add(new Phrase("MSISDN: ", boldFont));
        customerInfo.add(new Phrase(details.getMsisdn() + "\n", normalFont));
        
        customerInfo.add(new Phrase("Email: ", boldFont));
        customerInfo.add(new Phrase((details.getEmail() != null ? details.getEmail() : "N/A") + "\n", normalFont));
        
        customerInfo.setSpacingAfter(15);
        document.add(customerInfo);
    }

    private void addInvoiceDetails(Document document, InvoiceDetailsDto details) {
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);
        
        addCellToTable(table, "Invoice Number:", boldFont);
        addCellToTable(table, String.valueOf(details.getInvoiceId()), normalFont);
        
        addCellToTable(table, "Invoice Date:", boldFont);
        addCellToTable(table, LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")), normalFont);
        
        addCellToTable(table, "Billing Cycle:", boldFont);
        addCellToTable(table, details.getBillingCycleId() != null ? details.getBillingCycleId() : "N/A", normalFont);
        
        addCellToTable(table, "Plan Name:", boldFont);
        addCellToTable(table, details.getPlanName() != null ? details.getPlanName() : "N/A", normalFont);
        
        document.add(table);
    }

    private void addChargesTable(Document document, InvoiceDetailsDto details) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(30);
        
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        PdfPCell cell1 = new PdfPCell(new Phrase("Description", headerFont));
        cell1.setBackgroundColor(Color.DARK_GRAY);
        cell1.setPadding(8);
        table.addCell(cell1);
        
        PdfPCell cell2 = new PdfPCell(new Phrase("Amount", headerFont));
        cell2.setBackgroundColor(Color.DARK_GRAY);
        cell2.setPadding(8);
        table.addCell(cell2);
        
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        
        addChargeRow(table, "Plan Charges", String.valueOf(details.getPlanCharges()), normalFont);
        addChargeRow(table, "Usage Charges", String.valueOf(details.getUsageCharges()), normalFont);
        addChargeRow(table, "Other Charges", String.valueOf(details.getOtherCharges()), normalFont);
        
        addChargeRow(table, "Discount (" + (details.getDiscountName() != null ? details.getDiscountName() : "N/A") + ")", 
                "-" + String.valueOf(details.getDiscountValue()), normalFont);
                
        addChargeRow(table, "Adjustments", String.valueOf(details.getAdjustments()), normalFont);
        addChargeRow(table, "Tax (" + (details.getTaxName() != null ? details.getTaxName() : "N/A") + ")", 
                String.valueOf(details.getTax()), normalFont);
                
        addChargeRow(table, "Payments Received", "-" + String.valueOf(details.getPayments()), normalFont);
        
        // Total Row
        PdfPCell totalDesc = new PdfPCell(new Phrase("Total Amount Due", boldFont));
        totalDesc.setPadding(8);
        table.addCell(totalDesc);
        
        PdfPCell totalAmt = new PdfPCell(new Phrase(String.valueOf(details.getBillDueAmt()), boldFont));
        totalAmt.setPadding(8);
        table.addCell(totalAmt);
        
        document.add(table);
    }

    private void addCellToTable(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addChargeRow(PdfPTable table, String description, String amount, Font font) {
        PdfPCell descCell = new PdfPCell(new Phrase(description, font));
        descCell.setPadding(8);
        table.addCell(descCell);
        
        String displayAmount = (amount != null && !amount.equals("null") && !amount.equals("-null")) ? amount : "0.00";
        PdfPCell amtCell = new PdfPCell(new Phrase(displayAmount, font));
        amtCell.setPadding(8);
        table.addCell(amtCell);
    }

    private void addFooter(Document document) {
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.GRAY);
        Paragraph footer = new Paragraph(FOOTER_MESSAGE, footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
}
