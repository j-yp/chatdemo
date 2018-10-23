package com.example.chat.config.resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="stomp")
public class StompUserResource {
	private final List<String> annoPackages = new ArrayList<>();
	
	public List<String> getAnnoPackages() {
		return annoPackages;
	}

	public void setAnnoPackages(List<String> annoPackages) {
		this.annoPackages.addAll(annoPackages);
	}
	
}
