package com.simplbox.indexer.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "emails")
@Builder
@Getter
@ToString
public class Email {
    @Id
    private String id;
    private String threadId;
    private String sender;
    private String subject;
    private String body;
    private String date;
    // Add other fields as needed

}