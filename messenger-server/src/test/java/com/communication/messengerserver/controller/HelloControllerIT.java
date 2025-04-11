package com.communication.messengerserver.controller;

import com.communication.messengerserver.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HelloControllerIT {

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void simpleDbTEst() {
        User user = new User();
        user.setUsername("Artem");
        User saved = mongoTemplate.save(user);
        User ret = mongoTemplate.findById(saved.getId(), User.class);

        assertEquals(ret.getUsername(), "Artem");
    }
}