package com.communication.messengerserver.chat;

import com.communication.messengerserver.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ChatRepository {

    private final MongoTemplate mongoTemplate;

    public String findFriendId(String chatId, String userId) {
        Query query = new Query(Criteria.where("id").is(chatId));

        Chat chat = mongoTemplate.findOne(query, Chat.class);

        if (chat == null) {
            throw new ResourceNotFoundException("Chat with id " + chatId + " does not exist");
        }

        if (chat.getUser1Id().equals(userId)) {
            return chat.getUser2Id();
        } else {
            return chat.getUser1Id();
        }
    }

    public List<Chat> findAllChatsForUser(String userId) {
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("user1Id").is(userId),
                Criteria.where("user2Id").is(userId)
        ));

        return mongoTemplate.find(query, Chat.class);
    }
}
