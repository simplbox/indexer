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
@RequestMapping("/bulk-index")
public class EmailIndexerController {
    @Autowired
    public EmailIndexerController(EmailIndexerService emailIndexerService) {
        this.emailIndexerService = emailIndexerService;
    }

    private final EmailIndexerService emailIndexerService;

    @GetMapping("/once/{user}")
    public ResponseEntity<?> indexEmails(@PathVariable String user) throws GeneralSecurityException, IOException {
        List<Email> emails = emailIndexerService.fetchEmails(user, true);
        emails.forEach(emailIndexerService::saveEmail);
        return ResponseEntity.ok().body("success");
    }

    @GetMapping("/daily/{user}")
    public ResponseEntity<?> indexEmailsDaily(@PathVariable String user) throws GeneralSecurityException, IOException {
        List<Email> emails = emailIndexerService.fetchEmails(user, false);
        emails.forEach(emailIndexerService::saveEmail);
        return ResponseEntity.ok().body("success");
    }
}
