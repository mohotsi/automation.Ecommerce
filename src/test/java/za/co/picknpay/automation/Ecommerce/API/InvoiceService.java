package za.co.picknpay.automation.Ecommerce.API;



import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;
import za.co.picknpay.automation.Ecommerce.models.product.Product;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class InvoiceService {

    public byte[] generateInvoice(String orderNumber, String address, List<Product> products) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // 1. Header Section
            document.add(new Paragraph("TAX INVOICE")
                    .setBold().setFontSize(20).setFontColor(ColorConstants.BLUE));
            document.add(new Paragraph("Order Number: " + orderNumber));
            document.add(new Paragraph("Shipping Address:\n" + address).setMarginBottom(20));

            // 2. Table Setup (3 Columns)
            Table table = new Table(UnitValue.createPercentArray(new float[]{60, 20, 20}))
                    .useAllAvailableWidth();

            // Header Cells
            List.of("Product Description", "Qty", "Price").stream()
                    .map(header -> new Cell().add(new Paragraph(header))
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold())
                    .forEach(table::addHeaderCell);

            // 3. STREAM: Populate Data Rows
            products.stream().forEach(product -> {
                table.addCell(new Cell().add(new Paragraph(product.getName())));
                table.addCell(new Cell().add(new Paragraph("1")));
                table.addCell(new Cell().add(new Paragraph("R " + product.getPrice())));
            });

            // 4. STREAM: Calculate Total
            double total = products.stream()
                    .mapToDouble(p -> Double.parseDouble(p.getPrice().replaceAll("[^\\d.]", "")))
                    .sum();

            // 5. Footer Row for Total
            table.addCell(new Cell(1, 2).add(new Paragraph("TOTAL AMOUNT")).setBold());
            table.addCell(new Cell().add(new Paragraph("R " + String.format("%.2f", total))).setBold());

            document.add(table);
            document.add(new Paragraph("\nThis is a system-generated invoice for your Pick n Pay Ecommerce order.")
                    .setFontSize(8).setItalic());

        } catch (Exception e) {
            throw new RuntimeException("CRITICAL: Failed to stream data into PDF", e);
        }

        return baos.toByteArray();
    }
}