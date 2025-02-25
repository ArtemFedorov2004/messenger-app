package com.communication.messengerserver.chat.chatmessage;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ChatMessageDto {

    private String chatId;

    private String messageId;

    private String senderId;

    private String content;

    private Date createdAt;

    public ChatMessage toChatMessage() {
        return ChatMessage.builder()
                .messageId(this.messageId)
                .senderId(this.senderId)
                .content(this.content)
                .createdAt(this.createdAt)
                .build();
    }
}
