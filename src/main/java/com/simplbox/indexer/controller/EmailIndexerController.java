package com.simplbox.indexer.controller;

import com.simplbox.indexer.service.EmailIndexerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/bulk-index")
public class EmailIndexerController {
    @Autowired
    public EmailIndexerController(EmailIndexerService emailIndexerService) {
        this.emailIndexerService = emailIndexerService;
    }

    private final EmailIndexerService emailIndexerService;

    @GetMapping("/daily/{user}")
    public ResponseEntity<?> indexEmails(@PathVariable String user) throws GeneralSecurityException, IOException, ExecutionException, InterruptedException {
        Future<Boolean> future = emailIndexerService.fetchEmails(user, 1, true);
        return Boolean.TRUE.equals(future.get()) ? ResponseEntity.ok().body("success. messages are being indexed.") : ResponseEntity.internalServerError().body("Failed");
    }

    @GetMapping("/once/{user}/{days}")
    public ResponseEntity<?> indexEmailsDaily(@PathVariable String user, @PathVariable int days) throws GeneralSecurityException, IOException, ExecutionException, InterruptedException {
        Future<Boolean> future = emailIndexerService.fetchEmails(user, days, false);
        return Boolean.TRUE.equals(future.get()) ? ResponseEntity.ok().body("success. messages are being indexed.") : ResponseEntity.internalServerError().body("Failed");
    }
}
