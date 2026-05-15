package za.co.monateRetail.automation.Ecommerce.Notifications.model;

import jakarta.mail.Message;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for interacting with an email provider.
 * This abstraction supports fetching, filtering, and parsing email content
 * regardless of the underlying protocol (IMAP, POP3, or API-based like Gmail/Graph).
 */
public interface Email {

    /**
     * Extracts a specific header value from a raw Mail Message.
     * * @param message The raw Jakarta Mail Message object.
     * @param headerName The name of the header (e.g., "Subject", "From", "Importance").
     * @return The string value of the header, or null if the header does not exist.
     */
    String getHeader(Message message, String headerName);

    /**
     * Retrieves all emails received after a specific point in time.
     * Useful for synchronization tasks or "incremental" fetches.
     * * @param top The cutoff timestamp.
     * @return A list of EmailMessage DTOs representing emails newer than the provided date.
     */
    List<EmailMessage> getEmailAfter(LocalDateTime top);

    /**
     * Fetches a fixed number of the most recent email messages.
     * * @param top The maximum number of messages to retrieve (e.g., the last 10).
     * @return A list of the most recent EmailMessage DTOs.
     */
    List<EmailMessage> getEmailMessages(int top);

    /**
     * Retrieves the plain text content from all messages within a specific conversation thread.
     * This is typically used to reconstruct a chat-like history of an email chain.
     * * @param threadId The unique identifier for the email thread (e.g., X-GM-THRID for Gmail).
     * @return A list of strings, where each string represents the text body of a message in the thread.
     */
    List<String> getTextFromMessage(String threadId);

    /**
     * Extracts the specific body content (often including HTML or sanitized parts)
     * from a specific thread or message identifier.
     * * @param threadID The unique identifier for the conversation.
     * @return A list of body contents (HTML or Plain Text) formatted for display.
     */
    List<String> getEmailBodyContent(String threadID);
}