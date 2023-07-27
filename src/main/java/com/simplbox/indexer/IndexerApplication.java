package com.simplbox.indexer;

import com.simplbox.indexer.service.EmailIndexerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IndexerApplication{

	public static void main(String[] args) {
		SpringApplication.run(IndexerApplication.class, args);
	}
}
