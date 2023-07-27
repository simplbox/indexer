package com.simplbox.indexer.controller;

import com.simplbox.indexer.model.Email;
import com.simplbox.indexer.service.EmailIndexerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
public class EmailIndexerController {
    @Autowired
    public EmailIndexerController(EmailIndexerService emailIndexerService) {
        this.emailIndexerService = emailIndexerService;
    }

    private final EmailIndexerService emailIndexerService;

    @GetMapping("/bulk-index/{user}")
    public ResponseEntity<?> indexEmails(@PathVariable String user) throws GeneralSecurityException, IOException {
        List<Email> emails = emailIndexerService.fetchEmails(user);
        emails.forEach(emailIndexerService::saveEmail);
        return ResponseEntity.ok().body("success");
    }
}
