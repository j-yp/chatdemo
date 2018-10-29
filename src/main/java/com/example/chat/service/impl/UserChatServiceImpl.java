package com.example.chat.service.impl;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.example.chat.config.utils.MessageUtil;
import com.example.chat.service.UserChatService;

@Service
public class UserChatServiceImpl implements UserChatService{

	@Override
	public void sendMessage(Message<?> message) {
		String toUserCode = MessageUtil.getToCode(message);
		
	}

}
