package com.simplbox.indexer.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.simplbox.indexer.model.Email;
import com.simplbox.indexer.repository.EmailRepository;
import com.simplbox.indexer.utils.EmailExtractionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;


@Service

public class EmailIndexerService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailIndexerService.class);
    private final GmailApiClient gmailApiClient;
    private final EmailRepository emailRepository;

    @Autowired
    public EmailIndexerService(GmailApiClient gmailApiClient, EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
        this.gmailApiClient = gmailApiClient;
    }

    public List<Email> fetchEmails(String user) throws IOException, GeneralSecurityException {


        Gmail gmailInstance = gmailApiClient.getGmailService();
        LOG.info("Loaded Gmail Account of user {}. Fetching Emails", user);
        List<Message> messages = gmailInstance.users().messages().list(user).execute().getMessages();
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

    public void saveEmail(Email email) {

        emailRepository.save(email);
        LOG.info("Indexed Email with id {}", email.getId());
    }


}

