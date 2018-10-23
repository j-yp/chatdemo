package com.example.chat.config.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import com.example.chat.config.resource.StompUserResource;

import java.util.List;

@Configuration
public class StompAnnotationMethodHandler implements ApplicationContextAware{
	private ApplicationContext applicationContext;
	
	@Autowired
	private StompUserResource stompUserResource;
	
	public void setApplicationContext(@Nullable ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Nullable
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	public List<Class<?>> getAnnoTationPackageClasses(){
        List<String> annoPackages = this.stompUserResource.getAnnoPackages();
        //annoPackages.stream().forEach();
        return null;
    }
}
