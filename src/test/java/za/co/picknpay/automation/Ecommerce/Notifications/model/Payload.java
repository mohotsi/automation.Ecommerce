package za.co.picknpay.automation.Ecommerce.Notifications.model;
import lombok.*;
import java.util.List;

/**
 * Represents the main content structure of an email.
 * A payload can contain the message content directly or be broken
 * down into multiple recursive 'parts' (Multipart message).
 */
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payload {

    /** The identifier for this specific message part. */
    private String partId;

    /** * The MIME type of the message part (e.g., "text/plain", "text/html", "multipart/alternative").
     * Crucial for filtering which part of the email to parse for automation data.
     */
    private String mimeType;

    /** The filename of the part, if this represents an attachment. */
    private String filename;

    /** * Key-value pairs containing email metadata (Subject, From, To, Date).
     * Use this to verify the sender or subject line in your tests.
     */
    private List<Headers> headers;

    /** * The actual content container for this payload level.
     * May be empty if 'parts' is populated instead.
     */
    private Body body;

    /** * A list of sub-parts. Standard for HTML emails which usually contain
     * both a plain text version and an HTML version.
     */
    private List<Part> parts;

    /**
     * Helper method for automation: Finds a header value by name.
     * * @param name The header key (e.g., "Subject")
     * @return The value of the header or null if not found.
     */
    public String getHeaderValue(String name) {
        if (headers == null) return null;
        return headers.stream()
                .filter(h -> h.getName().equalsIgnoreCase(name))
                .map(Headers::getValue)
                .findFirst()
                .orElse(null);
    }
}