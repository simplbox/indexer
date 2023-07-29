package com.simplbox.indexer.controller;

import com.simplbox.indexer.model.Email;
import com.simplbox.indexer.model.RequestPayload;
import com.simplbox.indexer.service.EmailFilterService;
import com.simplbox.indexer.service.EmailIndexerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
public class EmailFilterController {

    private final EmailFilterService emailFilterService;

    @Autowired
    public EmailFilterController(EmailFilterService emailFilterService) {
        this.emailFilterService = emailFilterService;
    }


    @PostMapping(value = "/filter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> indexEmails(@RequestBody RequestPayload requestPayload, @RequestParam("page") int page, @RequestParam("offset") int offset) {
        List<Email> emails = emailFilterService.filterEmails(requestPayload, page, offset);
        return ResponseEntity.ok().body(emails);
    }
}
