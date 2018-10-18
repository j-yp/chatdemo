package com.example.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private SimpUserRegistry userRegistry;

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

    @MessageMapping("/send1")
    @SendToUser("/topic/greeting")
    public String sendMessageToUser(@Payload String message) {
    	/*System.out.println("user::::"+message);
    	simpMessagingTemplate.convertAndSendToUser("123456", "/topic/greeting", message);*/
        System.out.println("接收到了用户信息" + message);
        return "你发送的用户消息为:" + message;
    }

    @MessageMapping("/send2")
    public void sendMessageToUser2(@Payload String message) {
        System.out.println("user::::"+message);
        int i = 0;
        for (SimpUser user : userRegistry.getUsers()) {
            System.out.println("用户" + i++ + "---" + user);
        }
        System.out.println("共有："+i);
        simpMessagingTemplate.convertAndSendToUser("123456", "/topic/greeting2", message);
    }

}
