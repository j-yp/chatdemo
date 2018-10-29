package com.example.chat.config.handler;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageMappingInfo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageTypeMessageCondition;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.example.chat.config.resource.StompUserResource;
import com.example.chat.config.utils.PackageUtil;
/**
 * 考虑使用cglib代理来实现。
 * @author wisdom
 *
 */
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
	
	private Map<String, Class<?>> annotationTypeNameMap = new HashMap<>();


	public List<Class<?>> getAnnoTypes(){
		return annoTypes;
	}
	
	public void setAnnoTypes(List<Class<?>> annoTypes) {
		this.annoTypes = annoTypes;
	}

	public void initAnnotationPackageClasses(){
        List<String> annoPackages = this.stompUserResource.getAnnoPackages();
        List<Class<?>> list = this.getClassesFromPackages(annoPackages);
        List<Class<?>> classList = list.stream().filter(clazz ->
        	clazz.isAnnotation() && clazz.getName().endsWith("Controller"))
				.collect(Collectors.toList());
        this.setAnnoTypes(classList);
        annotationTypeNameMap = this.annoTypes.stream().collect(Collectors.toMap(
        		c -> {
        			String name = c.getSimpleName(); 
        			String subName = name.substring(1);
        			String first = (name.charAt(0)+"").toLowerCase();
        			return  first + subName.replace("Controller", "");}
        		, clazz -> clazz.getClass()));
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
		return (this.findAnnotation(bean) != null || AnnotatedElementUtils.hasAnnotation(bean, Controller.class));
	}
	
	@Override
	@Nullable
	protected SimpMessageMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
		Class<?> annotation = this.findAnnotation(handlerType);
		List<String> annotationNames = new ArrayList<>();
		if(annotation != null) {
			annotationNames.add(annotation.getSimpleName());
		}
		MessageMapping messageAnn = AnnotatedElementUtils.findMergedAnnotation(method, MessageMapping.class);
		if (messageAnn != null) {
			MessageMapping typeAnn = AnnotatedElementUtils.findMergedAnnotation(handlerType, MessageMapping.class);
			// Only actually register it if there are destinations specified;
			// otherwise @MessageMapping is just being used as a (meta-annotation) marker.
			if (messageAnn.value().length > 0 || (typeAnn != null && typeAnn.value().length > 0)) {
				SimpMessageMappingInfo result = createMessageMappingCondition(messageAnn.value());
				if (typeAnn != null) {
					if(annotationNames.isEmpty()) {
						createMessageMappingCondition(typeAnn.value()).combine(result);
					}else {
						result = createMessageMappingCondition(annotationNames.toArray(new String[annotationNames.size()]))
									.combine(createMessageMappingCondition(typeAnn.value()).combine(result));
					}
				}
				return result;
			}
		}

		SubscribeMapping subscribeAnn = AnnotatedElementUtils.findMergedAnnotation(method, SubscribeMapping.class);
		if (subscribeAnn != null) {
			MessageMapping typeAnn = AnnotatedElementUtils.findMergedAnnotation(handlerType, MessageMapping.class);
			// Only actually register it if there are destinations specified;
			// otherwise @SubscribeMapping is just being used as a (meta-annotation) marker.
			if (subscribeAnn.value().length > 0 || (typeAnn != null && typeAnn.value().length > 0)) {
				SimpMessageMappingInfo result = createSubscribeMappingCondition(subscribeAnn.value());
				if (typeAnn != null) {
					if(annotationNames.isEmpty()) {
						createMessageMappingCondition(typeAnn.value()).combine(result);
					}else {
						result = createMessageMappingCondition(annotationNames.toArray(new String[annotationNames.size()]))
								.combine(createMessageMappingCondition(typeAnn.value()).combine(result));
					}
				}
				return result;
			}
		}

		return null;
	}
	
	private SimpMessageMappingInfo createMessageMappingCondition(String[] destinations) {
		String[] resolvedDestinations = resolveEmbeddedValuesInDestinations(destinations);
		return new SimpMessageMappingInfo(SimpMessageTypeMessageCondition.MESSAGE,
				new DestinationPatternsMessageCondition(resolvedDestinations, this.getPathMatcher()));
	}
	
	private SimpMessageMappingInfo createSubscribeMappingCondition(String[] destinations) {
		String[] resolvedDestinations = resolveEmbeddedValuesInDestinations(destinations);
		return new SimpMessageMappingInfo(SimpMessageTypeMessageCondition.SUBSCRIBE,
				new DestinationPatternsMessageCondition(resolvedDestinations, this.getPathMatcher()));
	}
	
	private Class<?> findAnnotation(Class<?> bean){
		Annotation[] annotations = bean.getAnnotations();
		for (Annotation annotation : annotations) {
			if(this.annoTypes.contains(annotation.getClass())) {
				return annotation.getClass();
			}
		}
		return null;
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
		lookupDestination = this.combinAnnotaitonTypeToUrl(lookupDestination, );
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
	
	private String combinAnnotaitonTypeToUrl(String destination, String askType) {
		Class<?> bean = this.annotationTypeNameMap.get(askType);
		return this.getPathMatcher().combine(bean.getSimpleName(), destination);
	}
	
}
