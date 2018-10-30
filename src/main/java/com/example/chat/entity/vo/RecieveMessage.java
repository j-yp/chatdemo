package com.example.chat.entity.vo;

import com.example.chat.entity.vo.Headers.RecieveType;

/**
 * 接收消息的格式
 * @author wisdom
 *
 */
public class RecieveMessage {
	//消息类型
	private RecieveType type;
	//从哪个用户发送
	private String from;
	//到客户端哪个对话，到联系人就直接是发送用户，到群就是哪个群
	private String to;
	//消息
	private String message;

	public RecieveType getType() {
		return type;
	}

	public void setType(RecieveType type) {
		this.type = type;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
