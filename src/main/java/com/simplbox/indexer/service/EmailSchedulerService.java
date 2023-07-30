package com.simplbox.indexer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

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

    @Scheduled(cron = "0 0 11 * * ?", zone = "Asia/Kolkata")
    public void indexDailyMails() throws GeneralSecurityException, IOException {
        emailIndexerService.fetchEmails(emailAddress, 1, false);
        LOG.info("Mails indexing method executed at 12:05 am UTC.");
    }
}
