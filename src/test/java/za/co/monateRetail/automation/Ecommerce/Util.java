package za.co.monateRetail.automation.Ecommerce;

// Path: za/co/shoprite/ecommerce/automation/playwright/Util.java

import com.microsoft.playwright.Page;
import lombok.val;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;

import java.io.*;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

// ... (imports)

/**
 * Global Utility class providing helper methods for UI, API, PDF, and Image processing.
 * This class serves as the central hub for reusable logic across the automation framework.
 */
public class Util {

    /**
     * Executes a Runnable action and silently swallows any exceptions.
     * Useful for non-critical teardown steps or "fire-and-forget" actions.
     */
    public static void Try(Runnable action) {
        try {
            action.run();
        } catch (Exception | AssertionError e) {
            // Silently handled to prevent non-critical failures from stopping the test
        }
    }

    /**
     * Generic functional wrapper that returns a value or null if an exception occurs.
     * Often used to check for element properties that might not exist in the DOM.
     */
    public static <T> T Try(Supplier<T> action) {
        try {
            return Optional.ofNullable(action.get()).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }





    // --- PDF PROCESSING SECTION ---

    /**
     * Converts a PDF file into an HTML file on disk.
     * Useful for verifying PDF content (Invoices/Statements) using standard CSS selectors or Jsoup.
     */
    public static void getPdfAsHTMLContent(File file) {
        try {
            PDDocument pdf = PDDocument.load(file);
            val htmlFilePath = file.getAbsolutePath().replaceAll("(.+?)\\.pdf", "$1") + ".html";
            Writer output = new PrintWriter(htmlFilePath, "utf-8");
            new PDFDomTree().writeText(pdf, output);
            pdf.close();
        } catch (IOException e) {
            // Error handling for corrupt or missing PDF files
        }
    }
    public static boolean eventually(BooleanSupplier action, int seconds, LocalDateTime localDateTime) {
        val endTime = (int) Duration.between(LocalDateTime.now(),localDateTime).getSeconds();
        val results=endTime>seconds?seconds-endTime:1;
        System.out.println("<End Time> = " + endTime);
        return eventually(action, (long) results);
    }



// Helper method for eventuallyWithTimeout

    public static boolean eventually(BooleanSupplier action) {
        val thisPage = new Util();
        return thisPage.eventually(action, 15L);
    }
    private static boolean eventually(BooleanSupplier action, LocalDateTime localDateTime) {
        System.out.println("<Starting Date Time> = "+localDateTime);
        return eventuallyHelper(action, localDateTime, false);
    }

    private static boolean eventuallyHelper(BooleanSupplier action, LocalDateTime localDateTime, boolean found) {
        return eventuallyHelperTail(action, localDateTime, found);
    }

        public static boolean eventually(BooleanSupplier action, Long seconds) {
       LocalDateTime temp=LocalDateTime.now().plusSeconds(seconds);
        return  eventually(action,temp);
    }
    /**
     * Tail-recursive implementation of the polling logic.
     * It logs remaining time to the console for better debugging during long waits.
     */
    private static boolean eventuallyHelperTail(BooleanSupplier action, LocalDateTime localDateTime, boolean found) {
        long left = localDateTime.minusSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .toEpochSecond(ZoneOffset.UTC);

        if (left % 5 == 0) { // Log every 5 seconds to reduce console noise
            System.out.println(String.format("Waiting... %d min %d sec remaining", (left / 60), (left % 60)));
        }

        sleep(100); // 100ms interval between checks

        try {
            if (action.getAsBoolean()) return true;
            if (localDateTime.isBefore(LocalDateTime.now())) return found;
            return eventuallyHelperTail(action, localDateTime, found); // Recursive call
        } catch (Exception e) {
            if (localDateTime.isBefore(LocalDateTime.now())) return found;
            return eventuallyHelperTail(action, localDateTime, found);
        }
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (Exception e){

        }
    }

    // --- MISC HELPERS ---

    /**
     * Helper to determine if the current Playwright page is in Desktop view.
     */
    public static boolean isDesktop(Page page) {
        return page.viewportSize().width > 768;
    }

    /**
     * Formats numbers to South African Rand style but uses Canada Locale as a base for currency symbols.
     */
    public static String getFormatRands(Double number) {
        NumberFormat nF = NumberFormat.getCurrencyInstance(Locale.CANADA);
        return nF.format(number).replace("$", "R");
    }
}