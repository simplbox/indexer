package com.simplbox.indexer.repository;

import com.simplbox.indexer.model.Email;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends MongoRepository<Email, String> {
    // Add custom query methods if needed
}

