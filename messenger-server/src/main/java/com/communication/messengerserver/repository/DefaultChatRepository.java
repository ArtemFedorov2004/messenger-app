package com.communication.messengerserver.repository;

import com.communication.messengerserver.entity.Chat;
import com.communication.messengerserver.entity.Message;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@RequiredArgsConstructor
@Repository
public class DefaultChatRepository implements ChatRepository {

    public static final String COLLECTION_NAME = "user_chats";

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Chat> findAllByMember(String name) {
        Criteria criteria = Criteria.where("member_names").in(name);

        Query query = new Query(criteria);

        return this.mongoTemplate.find(query, Chat.class);
    }

    @Override
    public boolean exists(String chatId) {
        Query query = new Query(Criteria.where("_id").is(chatId));

        return this.mongoTemplate.exists(query, Chat.class);
    }

    @Override
    public List<String> getMemberNames(String chatId) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("_id").is(chatId)),
                project("member_names").andExclude("_id")
        );

        AggregationResults<Document> results = this.mongoTemplate.aggregate(aggregation, COLLECTION_NAME, Document.class);

        if (results.getMappedResults().isEmpty()) {
            return List.of();
        }

        Document document = results.getMappedResults().getFirst();
        return document.getList("member_names", String.class);
    }

    private int findLastMessageIdInChat(String chatId) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("_id").is(chatId)),
                unwind("messages"),
                sort(Sort.by(Sort.Order.desc("messages._id"))),
                limit(1),
                project()
                        .and("messages._id").as("last_message_id")
                        .andExclude("_id")
        );

        AggregationResults<Document> results = this.mongoTemplate.aggregate(aggregation, COLLECTION_NAME, Document.class);

        if (results.getMappedResults().isEmpty()) {
            return 0;
        }

        Document document = results.getMappedResults().getFirst();
        return document.getInteger("last_message_id");
    }

    @Override
    public Message pushMessageToChat(String chatId, Message message) {
        int messageId = this.findLastMessageIdInChat(chatId) + 1;
        message.setId(messageId);

        Update update = new Update();
        update.push("messages", message);

        this.mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(chatId)), update, Chat.class);

        return message;
    }

    @Override
    public Optional<Message> findMessageInChat(String chatId, Integer messageId) {
        MatchOperation matchChat = Aggregation.match(Criteria.where("_id").is(chatId));

        UnwindOperation unwindMessages = Aggregation.unwind("messages");

        MatchOperation matchMessage = Aggregation.match(Criteria.where("messages._id").is(messageId));

        ReplaceRootOperation replaceRoot = Aggregation.replaceRoot("messages");

        Aggregation aggregation = Aggregation.newAggregation(
                matchChat,
                unwindMessages,
                matchMessage,
                replaceRoot
        );

        AggregationResults<Message> results = this.mongoTemplate.aggregate(
                aggregation,
                COLLECTION_NAME,
                Message.class
        );

        if (results.getMappedResults().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(results.getMappedResults().getFirst());
    }

    @Override
    public void updateMessageInChat(String chatId, Message message) {
        Query query = new Query(
                Criteria.where("_id").is(chatId)
                        .and("messages._id").is(message.getId())
        );

        Update update = new Update()
                .set("messages.$", message);

        this.mongoTemplate.updateFirst(query, update, Chat.class);
    }

    @Override
    public boolean deleteMessage(String chatId, Integer messageId) {
        Query query = Query.query(Criteria.where("_id").is(chatId));

        Update update = new Update().pull("messages", Query.query(Criteria.where("id").is(messageId)));

        UpdateResult result = this.mongoTemplate.updateFirst(query, update, Chat.class);

        return result.getModifiedCount() > 0;
    }
}
