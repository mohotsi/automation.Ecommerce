package za.co.picknpay.automation.Ecommerce.Notifications.model;

import lombok.*;

/**
 * Represents the actual content container of an email part.
 * This can hold the raw text, HTML, or metadata for an attachment.
 */
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Body {

    /**
     * The ID used to retrieve an attachment.
     * This is only present if the message part represents an actual file attachment.
     */
    private String attachmentId;

    /**
     * The size of the body data in bytes.
     */
    private Integer size;

    /**
     * The actual content of the email part, encoded in Base64URL.
     * Note: For automation, this must be decoded to retrieve
     * plain text (like OTPs) or HTML content.
     */
    private String data;

    /**
     * Helper method to decode the Base64URL encoded data string.
     * Useful in Playwright tests to quickly get the text content for assertions.
     * * @return The decoded string content, or null if data is empty.
     */
    public String getDecodedData() {
        if (this.data == null || this.data.isEmpty()) {
            return null;
        }
        return new String(java.util.Base64.getUrlDecoder().decode(this.data));
    }
}
