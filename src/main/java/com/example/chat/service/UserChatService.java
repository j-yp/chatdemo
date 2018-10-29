package com.example.chat.service;

import org.springframework.messaging.Message;

public interface UserChatService {

	void sendMessage(Message<?> message);

}
