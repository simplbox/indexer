package com.simplbox.indexer.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.simplbox.indexer.model.Email;
import com.simplbox.indexer.repository.EmailRepository;
import com.simplbox.indexer.utils.EmailExtractionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;


@Service
@EnableAsync
public class EmailIndexerService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailIndexerService.class);
    private final GmailApiClient gmailApiClient;
    private final EmailRepository emailRepository;
    private BlockingQueue<Email> messageQueue;

    @Autowired
    public EmailIndexerService(GmailApiClient gmailApiClient, EmailRepository emailRepository, BlockingQueue<Email> messageQueue) {
        this.emailRepository = emailRepository;
        this.gmailApiClient = gmailApiClient;
        this.messageQueue = messageQueue;

    }

    public BlockingQueue<Email> getMessageQueue() {
        return messageQueue;
    }

    @Async
    public CompletableFuture<Boolean> fetchEmails(String user, int days, boolean runOnce) {

        try {
            Gmail gmailInstance = gmailApiClient.getGmailServiceThroughOAuth();
            LOG.info("Loaded Gmail Account of user {}. Fetching Emails", user);
            String query = prepareDateSearchQuery(days, runOnce);
            ListMessagesResponse response;
            if (runOnce) {
                List<Message> messages = gmailInstance.users().messages().list(user).setMaxResults(200L).execute().getMessages();
                if (messages != null && !messages.isEmpty()) {
                    messageQueue.addAll(convertMessagesToEmail(gmailInstance, messages, user));
                }
            } else {
                String nextPageToken = null;
                do {
                    if (nextPageToken == null) {
                        response = gmailInstance.users().messages().list(user).setQ(query).setMaxResults(200L).execute();
                    } else {
                        response = gmailInstance.users().messages().list(user).setQ(query).setMaxResults(200L)
                                .setPageToken(nextPageToken).execute();
                    }
                    List<Message> messages = response.getMessages();
                    if (messages != null && !messages.isEmpty()) {
                        messageQueue.addAll(convertMessagesToEmail(gmailInstance, messages, user));
                    }
                    nextPageToken = response.getNextPageToken();
                } while (nextPageToken != null);
            }
            return CompletableFuture.completedFuture(true);
        } catch (IOException | GeneralSecurityException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public String prepareDateSearchQuery(int days, boolean runOnce) {

        if (runOnce)
            return "";
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime lastDay = today.minusDays(days);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        ZoneId utcZone = ZoneId.of("UTC");
        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        ZonedDateTime utcZonedDateTimeToday = today.atZone(utcZone);
        ZonedDateTime utcZonedDateTimeLastDay = lastDay.atZone(utcZone);
        ZonedDateTime istZonedDateTimeToday = utcZonedDateTimeToday.withZoneSameInstant(istZone);
        ZonedDateTime istZonedDateTimeLastDay = utcZonedDateTimeLastDay.withZoneSameInstant(istZone);

        // Format the IST date as a string
        String istDateStringToday = istZonedDateTimeToday.format(formatter);
        String istDateStringLastDay = istZonedDateTimeLastDay.format(formatter);


        return "after:" + istDateStringLastDay + " " + "before:" + istDateStringToday;

    }


    public void saveEmail(Email email) {
        emailRepository.save(email);
        LOG.info("Indexed Email with id {}", email.getId());
    }

    public List<Email> convertMessagesToEmail(Gmail gmailInstance, List<Message> messages, String user) {
        return messages.stream()
                .map(
                        email ->
                        {
                            // Fetch the full content of each message using the `get()` method

                            try {
                                return gmailInstance.users().messages().get(user, email.getId()).execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                )
                .filter(Objects::nonNull) // emails returning null payload are ignored
                .map(
                        fullEmail ->
                                // Now you have the full message content, including headers and body
                                // You can extract the required information as needed
                        {
                            // Extract the relevant data from the email Message object
                            String id = fullEmail.getId();
                            String threadId = fullEmail.getThreadId();
                            String sender = EmailExtractionUtils.extractSender(fullEmail);
                            String subject = EmailExtractionUtils.getHeaderValue(fullEmail, "Subject");
                            String body = EmailExtractionUtils.getBody(fullEmail.getPayload());
                            String date = EmailExtractionUtils.getHeaderValue(fullEmail, "Date");
                            // Extract more fields as needed

                            // Create an Email object and save it to MongoDB using the repository
                            return Email.builder().id(id).threadId(threadId).sender(sender).subject(subject).body(body).date(date).build();
                            // Set more fields as needed
                        }
                ).toList();
    }


}

