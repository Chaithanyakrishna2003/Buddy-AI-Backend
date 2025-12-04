package com.dealshare.buddyai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class BuddyAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuddyAiApplication.class, args);
    }
}
