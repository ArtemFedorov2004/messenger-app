package com.communication.messengerserver.chat;

import com.communication.messengerserver.chat.chatmessage.ChatMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document
public class Chat {

    @Id
    private String id;

    @Transient
    private String title;

    private String user1Id;

    private String user2Id;

    private List<ChatMessage> messages;

    public void addMessage(ChatMessage chatMessage) {
        messages.add(chatMessage);
    }
}
