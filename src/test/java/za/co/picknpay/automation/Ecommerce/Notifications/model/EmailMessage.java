package za.co.picknpay.automation.Ecommerce.Notifications.model;

import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Represents a simplified view of an Email message for automation testing.
 * Designed to map closely to Gmail-style API responses.
 */
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailMessage {

    /** Unique identifier for the message */
    private String id;

    /** ID of the thread this message belongs to; useful for grouping conversations */
    private String threadId;

    /** List of labels applied to this message (e.g., 'INBOX', 'UNREAD', 'SENT') */
    private List<String> labelIds;

    /** A short preview of the message text */
    private String snippet;

    /** The full content structure (headers, body parts, attachments) */
    private Payload payload;

    /** Estimated size of the message in bytes */
    private Long sizeEstimate;

    /** ID used to track changes to the mailbox state */
    private String historyId;

    /** The internal server time the message was received (Unix epoch) */
    private Date internalDate;

    /** The entire raw message content (usually Base64 encoded) */
    private String raw;

    /**
     * Converts the internalDate (Date) into a LocalDateTime using the system default timezone.
     * Use this for assertions or filtering based on relative time.
     * * @return LocalDateTime representation of when the email was received.
     */
    public LocalDateTime getReceivedLocalDateTime() {
        if (internalDate == null) {
            return null;
        }
        return Instant.ofEpochMilli(internalDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Alias for getReceivedLocalDateTime() to support different naming conventions.
     * Note: In many frameworks, having both 'get' and non-'get' methods can
     * confuse JSON serializers; consider sticking to the getter format.
     */
    public LocalDateTime receivedLocalDateTime() {
        return getReceivedLocalDateTime();
    }
}
