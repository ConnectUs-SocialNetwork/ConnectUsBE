package com.example.ConnectUs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class ConnectUsApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConnectUsApplication.class, args);
	}

}
