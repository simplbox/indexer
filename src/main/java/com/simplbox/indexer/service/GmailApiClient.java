package com.simplbox.indexer.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GmailApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(GmailApiClient.class);
    private static final String APPLICATION_NAME = "Simplbox";
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public Gmail getGmailService() throws GeneralSecurityException, IOException {
        LOG.info("Establishing http connection to Gmail");
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        LOG.info("Credential Authorised. Connection Established");
        return new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential authorize(HttpTransport httpTransport) throws IOException {
        // Load credentials from the JSON file you downloaded
        LOG.info("Loading credentials");
        InputStream inputStream = GmailApiClient.class.getResourceAsStream("/static/client_secret_542582741095-rb23fi6kp2kg0q3657v6f60gm1ca15vg.apps.googleusercontent.com.json");
        assert inputStream != null;
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream));

        // Set up the authorization code flow for installed applications.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .build();

        // Set up the local server receiver to handle the authorization callback.
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        // Authorize the application and get the credentials.
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
