package com.simplbox.indexer;

import com.simplbox.indexer.repository.EmailRepository;
import com.simplbox.indexer.service.EmailIndexerService;
import com.simplbox.indexer.service.MessageConsumer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableScheduling
public class IndexerApplication {

    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private EmailIndexerService emailIndexerService;

    public static void main(String[] args) {
        SpringApplication.run(IndexerApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Start the message consumer thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new MessageConsumer(emailRepository, emailIndexerService.getMessageQueue()));
    }
}
