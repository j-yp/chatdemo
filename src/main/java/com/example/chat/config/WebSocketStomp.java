package com.example.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.example.chat.config.Intercptor.UserInterceptor;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketStomp implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket");
        registry.addEndpoint("/websocket")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //这里是绑定代理的路径可以绑定/topic 与/queue
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("192.168.100.99")
                .setRelayPort(61613)
                .setClientLogin("admin")
                .setClientPasscode("admin")
                .setSystemLogin("admin")
                .setSystemPasscode("admin");
		/*registry.enableSimpleBroker("/topic", "/queue")
				.setHeartbeatValue(new long[] {10000l, 10000l})
				.setTaskScheduler(new DefaultManagedTaskScheduler());*/
        //配置前缀, 有这些前缀的会被到有@SubscribeMapping与@MessageMapping的业务方法拦截

        //可以这么理解，这里是发送到系统的url前缀，客户端发送到系统都需要这个，系统接受可以省略
        registry.setApplicationDestinationPrefixes("/app");
        //这里是发送给用户的url前缀，用户订阅相应路径需要，系统发送给用户可以不需要这个，默认为user
        //registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(32)
                .maxPoolSize(200)
                .queueCapacity(10000);
        registration.interceptors(createUserInterceptor());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(100)
                .maxPoolSize(400)
                .queueCapacity(20000);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setSendTimeLimit(15*1000)
                .setSendBufferSizeLimit(512*1024);
    }

    @Bean
    public UserInterceptor createUserInterceptor(){
        return new UserInterceptor();
    }
}
