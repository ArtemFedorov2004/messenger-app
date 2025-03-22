package com.communication.messengerserver.repository;

import com.communication.messengerserver.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
