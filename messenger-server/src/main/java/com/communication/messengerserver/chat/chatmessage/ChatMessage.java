package com.communication.messengerserver.chat.chatmessage;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private String messageId;

    private String senderId;

    private String content;

    private Date createdAt;

    public void generateMessageId() {
        this.messageId = UUID.randomUUID().toString();
    }
}
