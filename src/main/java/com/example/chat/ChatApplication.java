package com.example.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(value={"com.example.chat.mapper"})
public class ChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class);
    }
}
