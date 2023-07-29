package com.simplbox.indexer.service;

import com.simplbox.indexer.model.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class EmailSchedulerService {

    private final EmailIndexerService emailIndexerService;
    private static final Logger LOG = LoggerFactory.getLogger(EmailSchedulerService.class);
    @Value("${user.email}")
    private String emailAddress;

    @Autowired
    public EmailSchedulerService(EmailIndexerService emailIndexerService) {
        this.emailIndexerService = emailIndexerService;
    }

    @Scheduled(cron = "0 5 0 * * ?", zone = "UTC")
    public void indexDailyMails() throws GeneralSecurityException, IOException {
        List<Email> emails = emailIndexerService.fetchEmails(emailAddress, false);
        emails.forEach(emailIndexerService::saveEmail);
        LOG.info("Mails indexed method executed at 12:05 am UTC.");
    }
}
