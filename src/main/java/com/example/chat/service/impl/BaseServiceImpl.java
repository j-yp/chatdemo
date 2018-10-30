package com.example.chat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.chat.service.BaseService;

public class BaseServiceImpl implements BaseService{
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	protected void message() {
		
	}
	
	protected enum MessageType{
		USER, GROUP;
	}
}
