package com.simplbox.indexer.utils;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.util.List;

public class EmailExtractionUtils {

    public static String extractSender(Message message) {
        List<MessagePartHeader> headers = message.getPayload().getHeaders();
        for (MessagePartHeader header : headers) {
            if (header.getName().equalsIgnoreCase("From")) {
                return header.getValue();
            }
        }
        return null; // Sender not found in headers
    }
    public static String getHeaderValue(Message message, String headerName) {
        for (MessagePartHeader header : message.getPayload().getHeaders()) {
            if (header.getName().equalsIgnoreCase(headerName)) {
                return header.getValue();
            }
        }
        return null;
    }

    public static String getBody(MessagePart payload) {
        if (payload.getBody().getData() != null) {
            return new String(Base64.decodeBase64(payload.getBody().getData()));
        } else if (payload.getParts() != null) {
            for (MessagePart part : payload.getParts()) {
                String body = getBody(part);
                if (body != null) {
                    return body;
                }
            }
        }
        return null;
    }
}
