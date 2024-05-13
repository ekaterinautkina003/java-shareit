package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

//@PropertySource("file:shareIt-server/src/main/resources/application.properties")
@SpringBootApplication
public class ShareItServer {
    public static void main(String[] args) {
        SpringApplication.run(ShareItServer.class, args);
    }
}