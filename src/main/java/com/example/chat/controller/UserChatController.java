package com.example.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.example.chat.service.UserChatService;

@Controller
@MessageMapping("/user")
public class UserChatController {
	@Autowired
	private UserChatService userChatService;
	
	@MessageMapping("/message")
	public void sendMessage(Message<?> message) {
		userChatService.sendMessage(message);
	}
}
