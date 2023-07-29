package com.simplbox.indexer.repository;

import com.simplbox.indexer.model.Email;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EmailRepository extends MongoRepository<Email, String> {
    // Add custom query methods if needed
    List<Email> findBySenderContainingIgnoreCase(String sender);

    List<Email> findByDate(Date date);

    List<Email> findBySubjectContainingIgnoreCase(String keyword);

    List<Email> findByBodyContainingIgnoreCase(String keyword);
}

