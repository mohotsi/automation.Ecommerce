package za.co.monateRetail.automation.Ecommerce.Notifications.model;

import com.itextpdf.awt.geom.misc.Messages;
import lombok.*;
import java.util.List;

/**
 * Represents a collection of email summaries returned from a search or list request.
 * This is the wrapper used to handle pagination and bulk message discovery.
 */
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Inbox {

    /**
     * A list of message summaries.
     * Note: In the Gmail API, these 'Messages' objects usually only contain
     * the 'id' and 'threadId'. You must perform a follow-up fetch per ID
     * to get the full EmailMessage content.
     */
    private List<Messages> messages;

    /**
     * The token used to retrieve the next page of results.
     * In automation, if you are looking for an older email and it's not in the
     * first 'messages' list, you would pass this token into your next API call.
     */
    private String nextPageToken;

    /**
     * An estimated count of the total messages that matched the query.
     * Useful for assertions to verify that an email was actually delivered
     * (e.g., resultSizeEstimate > 0).
     */
    private Integer resultSizeEstimate;
}