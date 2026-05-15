package za.co.monateRetail.automation.Ecommerce.Notifications;


import com.google.gson.Gson;
import io.cucumber.spring.ScenarioScope;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.ComparisonTerm;
import jakarta.mail.search.HeaderTerm;
import jakarta.mail.search.ReceivedDateTerm;
import lombok.val;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import za.co.monateRetail.automation.Ecommerce.Notifications.model.Email;
import za.co.monateRetail.automation.Ecommerce.Notifications.model.EmailMessage;
import za.co.monateRetail.automation.Ecommerce.config.Thread.Customer;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service("Gmail IMAPS")
@Primary
@ScenarioScope
public class ImapEmail implements Email {
    @Autowired

    private Customer customer;


    @Value("${gmail.host}")
    private String host;
    @Value("${gmail.port}")
    private String port;

    @Autowired
    Gson gson;


    public List<EmailMessage> getEmailAfter(LocalDateTime localDateTime) {
        try {
            // Create properties for the IMAP connection
            Properties props = new Properties();
            props.put("mail.imap.host", "imap.gmail.com");
            props.put("mail.imap.port", "993");
            props.put("mail.imap.ssl.enable", "true");
            props.setProperty("mail.imaps.timeout", "30000");
            // Get the IMAP session
            Session session = Session.getInstance(props);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            val instant=localDateTime.atZone(ZoneId.systemDefault()).toInstant();

            // Get all the email messages in the INBOX
            // Sort the messages by received date in descending order
            Message[] messages = inbox.search(new ReceivedDateTerm((ComparisonTerm.GE), Date.from(instant)));


// Limit the number of messages
            val  topMessages = Arrays.stream(Arrays.copyOfRange(messages, 0, 100)).parallel()
                    .filter(msg->msg!=null).collect(Collectors.toList());

            // Convert the messages to EmailMessage objects
            List<EmailMessage> emailMessages = new ArrayList<>();
            for (Message message : topMessages) {
                EmailMessage emailMessage = convertToEmailMessage(message);
                emailMessages.add(emailMessage);
            }

            // Close the IMAP connection and return the email messages
            inbox.close(false);
            return emailMessages.stream().sorted(Comparator.comparing((EmailMessage msg)->msg.receivedLocalDateTime())
                            .reversed())
                    .limit(100).collect(Collectors.toList());
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    private Store store;
    @PostConstruct
    public void init() throws Exception {
        if (store != null) {
            try {
                store.close();
            } catch (MessagingException e) {
                // Handle exception
            }
        }
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.host", "imap.gmail.com");
        props.setProperty("mail.imaps.port", String.valueOf("993"));
        props.setProperty("mail.imaps.ssl.enable", "true");
        props.setProperty("mail.imaps.timeout", "30000");
        Session session = Session.getDefaultInstance(props, null);
        store = session.getStore("imaps");

        store.connect(customer.getEmail(), customer.getEmailSecretKey());
    }
    @PreDestroy
    public void closeConnection() {
        if (store != null) {
            try {
                store.close();
            } catch (MessagingException e) {
                // Handle exception
            }
        }
    }

    @Override
    public List<EmailMessage> getEmailMessages(int top) {
        try {
            // Create properties for the IMAP connection


            // Get the INBOX folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Get all the email messages in the INBOX
            // Sort the messages by received date in descending order
            Message[] messages = inbox.search(new ReceivedDateTerm((ComparisonTerm.GE), new Date()));


// Limit the number of messages
            val  topMessages = Arrays.stream(Arrays.copyOfRange(messages, 0, top)).parallel()
                    .filter(msg->msg!=null).collect(Collectors.toList());

            // Convert the messages to EmailMessage objects
            List<EmailMessage> emailMessages = new ArrayList<>();
            for (Message message : topMessages) {
                EmailMessage emailMessage = convertToEmailMessage(message);
                emailMessages.add(emailMessage);
            }

            // Close the IMAP connection and return the email messages
            inbox.close(false);
            return emailMessages.stream().sorted(Comparator.comparing((EmailMessage msg)->msg.receivedLocalDateTime()).reversed())
                    .limit(top).collect(Collectors.toList());
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getEmailBodyContent(String threadId) {
        try {
            // Create properties for the IMAP connection
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");
            props.setProperty("mail.imaps.host", "imap.gmail.com");
            props.setProperty("mail.imaps.port", "993");

            // Enable SSL/TLS for secure connection
            props.setProperty("mail.imaps.ssl.enable", "true");

            props.setProperty("mail.imaps.fetchsize", "100"); // adjust this value as needed
            props.setProperty("mail.imaps.timeout", "30000"); // adjust this value as needed
            // Get the session
            Session session = Session.getDefaultInstance(props, null);

            // Connect to the IMAP store
            Store store = session.getStore("imaps");
            store.connect(customer.getEmail(), customer.getEmailSecretKey());

            // Get the thread messages
            List<Message> threadMessages = getThreadMessages(store,threadId);

            // Convert the messages to EmailMessage objects





            val results= threadMessages.parallelStream().map(msg->getEmailContent(msg)).collect(Collectors.toList());
            // Close the IMAP connection

            return results;
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            return Collections.emptyList();
        }


    }
    private List<Message> getThreadMessages(Store store, String threadId) {
        // Create properties for the IMAP connection


        List<Message> threadMessages = new ArrayList<>();
        try {
            // Open the INBOX folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Search for messages with the given thread ID
            Message[] messages = inbox.search(new HeaderTerm("Message-ID", threadId));

            // Add the messages to the list
            threadMessages = Arrays.asList(messages);
        } catch (Exception e) {
            // Handle any exceptions
            new Exception(e);
        }
        return threadMessages;
    }

    private EmailMessage convertToEmailMessage(Message message)  {
        String id = "";
        String threadId = getHeader(message,"Message-ID");
        String snippet = getHeader(message,"Subject");
        List<String> labelIds = Arrays.asList(message.getFolder().getFullName());
        String historyId = Integer.toString(message.getMessageNumber());
        Date internalDate = null;
        try {
            internalDate = message.getReceivedDate();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        String raw = "";


        // Build the EmailMessage object using the obtained values
        return EmailMessage.builder()
                .id(id)
                .threadId(threadId)
                .snippet(snippet)
                .labelIds(labelIds)
                .historyId(historyId)
                .internalDate(internalDate)
                .raw(raw)
                .build();
    }
    private String getSnippetFromMessage(Message message) {
        String content = getTextFromMessage(message);
        return content;
    }
    public  String getTextFromMessage(Message message)  {



        val text= Jsoup.parse(getEmailContent(message)).text();
        String snippet = StringUtils.abbreviate(text, 100);
        return snippet;
    }
    public List<String> getTextFromMessage(String threadId) {
        try {
            // Create properties for the IMAP connection

            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");
            props.setProperty("mail.imaps.host", "imap.gmail.com");
            props.setProperty("mail.imaps.port", "993");

            // Enable SSL/TLS for secure connection
            props.setProperty("mail.imaps.ssl.enable", "true");

            props.setProperty("mail.imaps.fetchsize", "100"); // adjust this value as needed
            props.setProperty("mail.imaps.timeout", "30000"); // adjust this value as needed
            // Get the session
            Session session = Session.getDefaultInstance(props, null);

            // Connect to the IMAP store
            Store store = session.getStore("imaps");
            store.connect(customer.getEmail(), customer.getEmailSecretKey());

            // Get the thread messages
            List<Message> threadMessages = getThreadMessages(store,threadId);

            // Convert the messages to EmailMessage objects





            val results= threadMessages.stream()
                    .map(msg->getTextFromMessage(msg)).collect(Collectors.toList());
            // Close the IMAP connection
            store.close();
            return results;
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            return Collections.emptyList();
        }


    }


    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart)  throws MessagingException, IOException {
        String result = "";
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                return result + "\n" + bodyPart.getContent(); // without return, same text appears twice in my tests
            }
            result += this.parseBodyPart(bodyPart);
        }
        return result;
    }
    private String getEmailContent(Message message) {
        try {
            if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                return getMimeMultipartContent(mimeMultipart);
            }
            else {
                return message.getContent().toString();
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    private String getMimeMultipartContent(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            result.append(parseBodyPart(bodyPart));
        }
        return result.toString();
    }

    private String parseBodyPart(BodyPart bodyPart) throws MessagingException, IOException {
        if (bodyPart.isMimeType("text/html")) {
            String content = bodyPart.getContent().toString();
            if (content.startsWith("=?")) { // Check if content is Base64 encoded
                content = new String(Base64.decodeBase64(content));
            }
            return content;
        }
        if (bodyPart.isMimeType("text/plain")) {
            return "\n" + bodyPart.getContent();
        }
        if (bodyPart.getContent() instanceof MimeMultipart) {
            String content= getMimeMultipartContent((MimeMultipart) bodyPart.getContent());
            return new String(Base64.decodeBase64(content));
        }
        if (bodyPart.getDisposition() != null && bodyPart.getDisposition().equals(BodyPart.ATTACHMENT)) {
            return "Attachment: " + bodyPart.getFileName();
        }
        return "";
    }
    public String getHeader(Message message, String headerName) {
        try {
            Enumeration<Header> headers= message.getAllHeaders();

            Map<String,String> heads=new HashMap<>();
            while(headers.hasMoreElements()){

                val header=headers.nextElement();
                heads.put(header.getName(),header.getValue());
            }
            val results=heads;
            heads.keySet().stream().forEach(System.out::println);
            return heads.get(headerName);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }




    private String getRawEmailMessage(Message message) throws Exception {
        // You need to implement this method to obtain the raw email message content
        // Reference the JavaMail API documentation for details on accessing message content
        return null;
    }
}
