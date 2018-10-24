package com.example.chat.config.handler;

import com.example.chat.config.resource.StompUserResource;
import com.example.chat.config.utils.PackageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StompAnnotationMethodHandler extends SimpAnnotationMethodMessageHandler {
	/**
	 * Create an instance of SimpAnnotationMethodMessageHandler with the given
	 * message channels and broker messaging template.
	 *
	 * @param clientInboundChannel  the channel for receiving messages from clients (e.g. WebSocket clients)
	 * @param clientOutboundChannel the channel for messages to clients (e.g. WebSocket clients)
	 * @param brokerTemplate        a messaging template to send application messages to the broker
	 */
	public StompAnnotationMethodHandler(SubscribableChannel clientInboundChannel, MessageChannel clientOutboundChannel, SimpMessageSendingOperations brokerTemplate) {
		super(clientInboundChannel, clientOutboundChannel, brokerTemplate);
	}


	@Autowired
	private StompUserResource stompUserResource;

	private List<Class<?>> annoTypes;


	public List<Class<?>> getAnnoTypes(){
		return annoTypes;
	}
	
	public void setAnnoTypes(List<Class<?>> annoTypes) {
		this.annoTypes = annoTypes;
	}

	public List<Class<?>> getAnnotationPackageClasses(){
        List<String> annoPackages = this.stompUserResource.getAnnoPackages();
        List<Class<?>> list = this.getClassesFromPackages(annoPackages);
        return list.stream().filter(clazz ->
        	clazz.isAnnotation() && clazz.getName().endsWith("Controller"))
				.collect(Collectors.toList());
    }
	
	public List<Class<?>> getClassesFromPackages(List<String> packages){
		List<Class<?>> list = packages.stream().collect(
				Collectors.reducing(new ArrayList<Class<?>>(), 
				packageName -> {
					try {
						return PackageUtil.getClassName(packageName).stream().map(className -> {
							try {
								return Class.forName(className);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
							return null;
						}).collect(Collectors.toList());
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}, 
				(classes1, classes2) -> {classes1.addAll(classes2); return classes1;}));
		return list;
	}

	@Override
	public boolean isHandler(Class<?> bean){
		return this.annoTypes.contains(bean);
	}

	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		String destination = getDestination(message);
		if (destination == null) {
			return;
		}
		String lookupDestination = getLookupDestination(destination);
		if (lookupDestination == null) {
			return;
		}

		MessageHeaderAccessor headerAccessor = MessageHeaderAccessor.getMutableAccessor(message);
		headerAccessor.setHeader(DestinationPatternsMessageCondition.LOOKUP_DESTINATION_HEADER, lookupDestination);
		headerAccessor.setLeaveMutable(true);
		message = MessageBuilder.createMessage(message.getPayload(), headerAccessor.getMessageHeaders());

		if (logger.isDebugEnabled()) {
			logger.debug("Searching methods to handle " +
					headerAccessor.getShortLogMessage(message.getPayload()) +
					", lookupDestination='" + lookupDestination + "'");
		}

		handleMessageInternal(message, lookupDestination);
		headerAccessor.setImmutable();
	}
}
