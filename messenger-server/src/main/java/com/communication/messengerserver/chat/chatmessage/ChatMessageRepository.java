package com.communication.messengerserver.chat.chatmessage;

import com.communication.messengerserver.chat.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChatMessageRepository {

    private final MongoTemplate mongoTemplate;

    public ChatMessage save(String chatId, ChatMessage chatMessage) {
        chatMessage.generateMessageId();

        Query query = new Query(Criteria.where("id").is(chatId));
        Update update = new Update();
        update.push("messages", chatMessage);

        mongoTemplate.updateFirst(query, update, Chat.class);

        Chat updatedChat = mongoTemplate.findOne(query, Chat.class);

        if (updatedChat == null || updatedChat.getMessages().isEmpty()) {
            throw new RuntimeException("Failed to insert the message into chat with id: " + chatId);
        }

        ChatMessage savedMessage = updatedChat.getMessages()
                .getLast();

        return savedMessage;
    }
}
