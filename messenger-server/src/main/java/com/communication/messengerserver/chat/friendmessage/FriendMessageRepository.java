package com.communication.messengerserver.chat.friendmessage;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface FriendMessageRepository extends MongoRepository<FriendMessage, String> {

    List<FriendMessage> findAllByIdIn(List<String> ids);
}
