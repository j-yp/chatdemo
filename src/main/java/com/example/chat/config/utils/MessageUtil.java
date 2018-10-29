package com.example.chat.config.utils;

import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public class MessageUtil {
	//获取message header上的toCode, 该id可以为groupCode, 或者userCode etc.
	@SuppressWarnings("rawtypes")
	public static String getToCode(Message<?> message) {
		Object row = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
		if(row instanceof Map) {
			return (String) ((Map) row).get("toCode");
		}
		return null;
	}
}
