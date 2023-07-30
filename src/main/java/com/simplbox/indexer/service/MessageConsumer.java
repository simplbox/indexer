package com.simplbox.indexer.service;

import com.simplbox.indexer.model.Email;
import com.simplbox.indexer.repository.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;

@Service
public class MessageConsumer implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class);
    private final EmailRepository repository;
    private BlockingQueue<Email> messageQueue;

    public MessageConsumer(EmailRepository repository, BlockingQueue<Email> messageQueue) {
        this.repository = repository;
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Consume messages from the queue and save them into the database
                Email email = messageQueue.take();

                repository.save(email);
                LOG.info("Indexed Email with id {}", email.getId());
                // Assuming you have a method to save the message in your repository
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

