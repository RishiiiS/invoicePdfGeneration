package com.telecom.invoicePdfGeneration.services.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.telecom.invoicePdfGeneration.dto.internal.InvoiceDetailsDto;
import com.telecom.invoicePdfGeneration.services.PdfGenerationService;
import com.telecom.invoicePdfGeneration.util.AmountToWords;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGenerationServiceImpl implements PdfGenerationService {

    // Telecom Company Colors
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);

    @Override
    public byte[] generateInvoicePdf(InvoiceDetailsDto invoiceDetails) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            addHeader(document);
            addCustomerAndInvoiceInfo(document, invoiceDetails);
            addChargesTable(document, invoiceDetails);
            addTotalSummary(document, invoiceDetails);
            addFooter(document);
            
            document.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while generating PDF", e);
        }
    }

    private void addHeader(Document document) throws Exception {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 1});
        
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(PdfPCell.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        
        try {
            Image logo;
            try {
                logo = Image.getInstance(new ClassPathResource("static/images/logo.png").getURL());
            } catch (Exception e) {
                logo = Image.getInstance(new ClassPathResource("images/logo.png").getURL());
            }
            logo.scaleToFit(200, 120);
            logoCell.addElement(logo);
        } catch (Exception e) {
            // Fallback if logo not found
            Font fallbackFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_COLOR);
            logoCell.addElement(new Paragraph("Telecom Company", fallbackFont));
        }
        headerTable.addCell(logoCell);
        
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, PRIMARY_COLOR);
        PdfPCell titleCell = new PdfPCell(new Phrase("POST PAID INVOICE", titleFont));
        titleCell.setBorder(PdfPCell.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(titleCell);
        
        document.add(headerTable);
        
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(PRIMARY_COLOR);
        separator.setLineWidth(1.5f);
        document.add(new Paragraph(" "));
        document.add(separator);
        document.add(new Paragraph(" "));
    }

    private void addCustomerAndInvoiceInfo(Document document, InvoiceDetailsDto details) throws Exception {
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1, 1});
        infoTable.setSpacingAfter(30);
        
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, PRIMARY_COLOR);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        
        // Left Column (Customer)
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(PdfPCell.NO_BORDER);
        
        PdfPTable leftInnerTable = new PdfPTable(2);
        leftInnerTable.setWidthPercentage(100);
        leftInnerTable.setWidths(new float[]{1, 1});
        
        String fullName = formatString(details.getFirstName()) + " " + formatString(details.getLastName());
        addCellToTable(leftInnerTable, "Customer Name:", boldFont, Element.ALIGN_LEFT);
        addCellToTable(leftInnerTable, fullName.trim(), normalFont, Element.ALIGN_LEFT);
        
        addCellToTable(leftInnerTable, "MSISDN:", boldFont, Element.ALIGN_LEFT);
        addCellToTable(leftInnerTable, formatString(details.getMsisdn()), normalFont, Element.ALIGN_LEFT);
        
        addCellToTable(leftInnerTable, "Email:", boldFont, Element.ALIGN_LEFT);
        addCellToTable(leftInnerTable, formatString(details.getEmail()), normalFont, Element.ALIGN_LEFT);
        
        leftCell.addElement(leftInnerTable);
        infoTable.addCell(leftCell);
        
        // Right Column (Invoice)
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(PdfPCell.NO_BORDER);
        
        PdfPTable rightInnerTable = new PdfPTable(2);
        rightInnerTable.setWidthPercentage(100);
        rightInnerTable.setWidths(new float[]{1, 1});
        
        addCellToTable(rightInnerTable, "Invoice Number:", boldFont, Element.ALIGN_RIGHT);
        addCellToTable(rightInnerTable, String.valueOf(details.getInvoiceId()), normalFont, Element.ALIGN_RIGHT);
        
        addCellToTable(rightInnerTable, "Invoice Date:", boldFont, Element.ALIGN_RIGHT);
        addCellToTable(rightInnerTable, LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")), normalFont, Element.ALIGN_RIGHT);
        
        addCellToTable(rightInnerTable, "Billing Cycle:", boldFont, Element.ALIGN_RIGHT);
        addCellToTable(rightInnerTable, formatString(details.getBillingCycleId()), normalFont, Element.ALIGN_RIGHT);
        
        addCellToTable(rightInnerTable, "Plan Name:", boldFont, Element.ALIGN_RIGHT);
        addCellToTable(rightInnerTable, formatString(details.getPlanName()), normalFont, Element.ALIGN_RIGHT);
        
        rightCell.addElement(rightInnerTable);
        infoTable.addCell(rightCell);
        
        document.add(infoTable);
    }

    private void addChargesTable(Document document, InvoiceDetailsDto details) throws Exception {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 1});
        table.setSpacingAfter(20);
        
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        
        PdfPCell cell1 = new PdfPCell(new Phrase("Description", headerFont));
        cell1.setBackgroundColor(PRIMARY_COLOR);
        cell1.setPadding(10);
        cell1.setBorderColor(Color.WHITE);
        table.addCell(cell1);
        
        PdfPCell cell2 = new PdfPCell(new Phrase("Amount (USD)", headerFont));
        cell2.setBackgroundColor(PRIMARY_COLOR);
        cell2.setPadding(10);
        cell2.setBorderColor(Color.WHITE);
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell2);
        
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        
        addChargeRow(table, "Plan Charges", formatAmount(details.getPlanCharges()), normalFont);
        addChargeRow(table, "Usage Charges", formatAmount(details.getUsageCharges()), normalFont);
        addChargeRow(table, "Other Charges", formatAmount(details.getOtherCharges()), normalFont);
        
        // Tax Section
        addChargeRow(table, "Tax Details", formatString(details.getTaxName()), normalFont);
        addChargeRow(table, "Tax Rate", formatString(details.getTaxValue()), normalFont);
        addChargeRow(table, "Tax Total", formatAmount(details.getTax()), normalFont);
        
        // Discount Section
        String discountDesc = "Discounts";
        if (details.getDiscountName() != null && !details.getDiscountName().isEmpty()) {
            discountDesc = details.getDiscountName();
        }
        addChargeRow(table, discountDesc, formatNegativeAmount(details.getDiscountValue()), normalFont);
        
        addChargeRow(table, "Adjustments", formatAmount(details.getAdjustments()), normalFont);
        // addChargeRow(table, "Payments Received", formatNegativeAmount(details.getPayments()), normalFont);
        
        document.add(table);
    }

    private void addTotalSummary(Document document, InvoiceDetailsDto details) throws Exception {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 1});
        table.setSpacingBefore(10);
        table.setSpacingAfter(20);
        
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, PRIMARY_COLOR);
        
        PdfPCell descCell = new PdfPCell(new Phrase("TOTAL AMOUNT DUE", totalFont));
        descCell.setBackgroundColor(LIGHT_GRAY);
        descCell.setPadding(12);
        descCell.setBorderColor(PRIMARY_COLOR);
        descCell.setBorderWidthTop(1.5f);
        descCell.setBorderWidthBottom(1.5f);
        descCell.setBorderWidthLeft(0);
        descCell.setBorderWidthRight(0);
        table.addCell(descCell);
        
        PdfPCell amtCell = new PdfPCell(new Phrase(formatAmount(details.getBillDueAmt()), totalFont));
        amtCell.setBackgroundColor(LIGHT_GRAY);
        amtCell.setPadding(12);
        amtCell.setBorderColor(PRIMARY_COLOR);
        amtCell.setBorderWidthTop(1.5f);
        amtCell.setBorderWidthBottom(1.5f);
        amtCell.setBorderWidthLeft(0);
        amtCell.setBorderWidthRight(0);
        amtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(amtCell);
        
        document.add(table);
        
        Font wordsFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.DARK_GRAY);
        Paragraph wordsPara = new Paragraph("Amount in Words: " + AmountToWords.convert(details.getBillDueAmt()), wordsFont);
        wordsPara.setSpacingAfter(40);
        document.add(wordsPara);
    }

    private void addFooter(Document document) throws Exception {
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(Color.LIGHT_GRAY);
        separator.setLineWidth(1);
        document.add(separator);
        document.add(new Paragraph(" "));
        
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);
        
        Paragraph footer1 = new Paragraph("Thank you for choosing our Telecom Services.", footerFont);
        footer1.setAlignment(Element.ALIGN_CENTER);
        
        Paragraph footer2 = new Paragraph("This is a computer-generated invoice and does not require a signature.", footerFont);
        footer2.setAlignment(Element.ALIGN_CENTER);
        
        Paragraph footer3 = new Paragraph("Customer Support | support@telecom.com | +1-800-000-0000", footerFont);
        footer3.setAlignment(Element.ALIGN_CENTER);
        
        document.add(footer1);
        document.add(footer2);
        document.add(footer3);
    }

    private void addCellToTable(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(4);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    private void addChargeRow(PdfPTable table, String description, String amount, Font font) {
        PdfPCell descCell = new PdfPCell(new Phrase(description, font));
        descCell.setPadding(8);
        descCell.setBorderColor(Color.LIGHT_GRAY);
        table.addCell(descCell);
        
        PdfPCell amtCell = new PdfPCell(new Phrase(amount, font));
        amtCell.setPadding(8);
        amtCell.setBorderColor(Color.LIGHT_GRAY);
        amtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(amtCell);
    }

    private String formatAmount(Object amountObj) {
        if (amountObj == null) {
            return "$0.00";
        }
        String val = amountObj.toString();
        if (val.isEmpty() || val.equals("null")) {
            return "$0.00";
        }
        try {
            BigDecimal amt = new BigDecimal(val);
            return "$" + String.format("%.2f", amt);
        } catch (Exception e) {
            return val;
        }
    }

    private String formatNegativeAmount(Object amountObj) {
        String formatted = formatAmount(amountObj);
        if (formatted.equals("$0.00") || formatted.equals("$0.00")) {
            return formatted;
        }
        return "-" + formatted;
    }

    private String formatString(String val) {
        return (val != null && !val.equals("null") && !val.isEmpty()) ? val : "N/A";
    }
}
