package com.example.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @MessageMapping("/send")
    @SendTo("/topic/chat")
    public String sendDemo(String message) {
        System.out.println("接收到了信息" + message);
        return "你发送的消息为:" + message;
    }

    @SubscribeMapping("/sub")
    @SendTo("/topic/chat1")
    public String sub() {
        System.out.println("XXX用户订阅了我。。。");
        return "感谢你订阅了我。。。";
    }

    @MessageMapping("/sendMessage")
    @SendTo("/app/send")
    public String sendMessage(String message) {
        System.out.println("接收到了信息:" + message);
        return "你发送的消息为:" + message;
    }

}
