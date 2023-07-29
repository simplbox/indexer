package com.simplbox.indexer.service;

import com.simplbox.indexer.model.Email;
import com.simplbox.indexer.model.RequestPayload;
import com.simplbox.indexer.repository.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailFilterService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailFilterService.class);
    private final EmailRepository emailRepository;

    @Autowired
    public EmailFilterService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public List<Email> filterEmails(RequestPayload requestPayload, int page, int offset) {
        return emailRepository.findBySenderContainingIgnoreCase(requestPayload.getSender());
    }
}
