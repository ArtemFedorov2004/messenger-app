package com.communication.messengerserver.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;


public class DisabledCsrfChannelInterceptor implements ChannelInterceptor {

    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        return message;
    }
}
