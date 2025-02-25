package com.communication.messengerserver.websocket;

import com.communication.messengerserver.security.KeycloakJwtTokenConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final KeycloakJwtTokenConverter keycloakJwtTokenConverter;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Value("${allowed-origins}")
    private String[] allowedOrigins;

    public WebSocketConfig(KeycloakJwtTokenConverter keycloakJwtTokenConverter, JwtDecoder jwtDecoder) {
        this.keycloakJwtTokenConverter = keycloakJwtTokenConverter;

        this.jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        this.jwtAuthenticationProvider.setJwtAuthenticationConverter(keycloakJwtTokenConverter);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/user");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowedOrigins)
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null) {
                    throw new RuntimeException("StompHeaderAccessor is null");
                }

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

                    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                        throw new InvalidBearerTokenException("Bearer token not found in Authorization header");
                    }

                    String token = authorizationHeader.substring(7);
                    accessor.setUser(jwtAuthenticationProvider.authenticate(new BearerTokenAuthenticationToken(token)));
                }

                return message;
            }
        });
    }

    @Bean
    public DisabledCsrfChannelInterceptor csrfChannelInterceptor() {
        return new DisabledCsrfChannelInterceptor();
    }
}
