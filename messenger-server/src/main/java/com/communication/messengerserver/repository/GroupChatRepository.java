package com.communication.messengerserver.repository;

import com.communication.messengerserver.entity.GroupChat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface GroupChatRepository extends MongoRepository<GroupChat, String> {

    @Query("{ '_id' : ?1 }")
    @Update("{ '$push' : { 'members': ?0 }}")
    void findAndPushUserToMembersById(String userId, String chatId);

    @Query("{ '_id' : ?1 }")
    @Update("{ '$pull' : { 'members': ?0 } }")
    void findAndDeleteChatMemberById(String memberId, String chatId);

    @Query("{ '_id' : ?1 }")
    @Update("{ '$push' : { 'messages': ?0 }}")
    void findAndPushMessageById(String messageId, String chatId);

    @Query("{ 'messages': { '$in': [?0] } }")
    @Update("{ '$pull' : { 'messages': ?0 } }")
    void findAndDeleteMessageById(String messageId);
}
