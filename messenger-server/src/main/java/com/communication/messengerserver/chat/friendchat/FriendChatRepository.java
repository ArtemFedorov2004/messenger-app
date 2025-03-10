package com.communication.messengerserver.chat.friendchat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class FriendChatRepository {

    private final MongoTemplate mongoTemplate;

    public List<FriendChat> findChatsByParticipantId(String participantId) {
        Criteria criteria = Criteria.where("participantIds").in(participantId);

        Query query = new Query(criteria);

        return mongoTemplate.find(query, FriendChat.class);
    }

    public FriendChat save(FriendChat chat) {
        return mongoTemplate.save(chat);
    }

    public Optional<FriendChat> findByParticipantIds(List<String> participantIds) {
        Criteria criteria = Criteria.where("participantIds")
                .all(participantIds);

        Query query = new Query(criteria);

        FriendChat friendChat = mongoTemplate.findOne(query, FriendChat.class);

        return Optional.ofNullable(friendChat);
    }

    public void addMessageIdToChat(String messageId, List<String> participantIds) {
        Criteria criteria = Criteria.where("participantIds").all(participantIds);
        Query query = new Query(criteria);

        Update update = new Update();
        update.addToSet("messageIds", messageId);

        mongoTemplate.updateFirst(query, update, FriendChat.class);
    }
}
