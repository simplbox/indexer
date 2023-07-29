package com.simplbox.indexer.model;

import lombok.Getter;

@Getter
public class RequestPayload {
    private String sender;
    private String subject;
    private String body;
    private String date;
}
