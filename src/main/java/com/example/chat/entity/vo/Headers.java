package com.example.chat.entity.vo;

import java.util.HashMap;
import java.util.Map;

public class Headers {
	// 消息类型
	private RecieveType type;
	// 从哪个用户发送
	private String from;
	// 到客户端哪个对话，到联系人就直接是发送用户，到群就是哪个群
	private String to;
	
	private Map<String, Object> headerMap;

	public RecieveType getType() {
		return type;
	}

	public void setType(RecieveType type) {
		this.headerMap.put("type", type.name());
		this.type = type;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.headerMap.put("from", from);
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.headerMap.put("to", to);
		this.to = to;
	}

	public Map<String, Object> getHeaderMap() {
		return headerMap;
	}

	public Headers() {
		super();
		this.headerMap = new HashMap<>();
	}

	/**
	 * 消息类型，USER：联系人，GROUP：群
	 * 
	 * @author wisdom
	 *
	 */
	public enum RecieveType {
		USER, GROUP;
	}
}
