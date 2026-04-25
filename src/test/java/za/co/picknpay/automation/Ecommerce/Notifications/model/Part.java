package za.co.picknpay.automation.Ecommerce.Notifications.model;
import lombok.*;
import java.util.List;

/**
 * Represents a sub-component of an email message.
 * In a multipart email, the message is broken into several parts
 * (e.g., one for text/plain, one for text/html, and others for attachments).
 */
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Part {

    /**
     * The unique identifier for this specific part within the message hierarchy.
     */
    private String partId;

    /**
     * The MIME type of this part.
     * In automation, you typically search for "text/plain" for OTP extraction
     * or "text/html" for link clicking.
     */
    private String mimeType;

    /**
     * The name of the file, populated only if this part represents an attachment.
     */
    private String filename;

    /**
     * Metadata specific to this part (e.g., Content-Type, Content-ID for inline images).
     */
    private List<Headers> headers;

    /**
     * The actual content container for this part.
     * If this part is 'multipart/alternative', the body might be empty,
     * but the nested 'parts' (inherited via Payload logic) would contain the data.
     */
    private Body body;
}