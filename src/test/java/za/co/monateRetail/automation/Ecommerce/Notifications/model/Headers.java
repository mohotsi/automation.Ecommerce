package za.co.monateRetail.automation.Ecommerce.Notifications.model;


import lombok.*;

/**
 * Represents a single metadata entry (header) of an email message or part.
 * Common names include "Subject", "From", "To", and "Date".
 */
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Headers {

    /** * The name of the header (e.g., "From", "Subject", "Content-Type").
     * Case-insensitivity is common in email protocols but depends on your search logic.
     */
    private String name;

    /** * The value associated with the header name.
     * For "From", this might be "Shoprite <no-reply@shoprite.co.za>".
     */
    private String value;
}