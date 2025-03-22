package com.communication.messengerserver.repository;

import com.communication.messengerserver.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {
}
