package com.communication.messengerserver.controller;

import com.communication.messengerserver.config.TestingBeans;
import com.communication.messengerserver.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@Import(TestingBeans.class)
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
        log.info(ret.getUsername() + ret.getId());
        //throw new RuntimeException();
    }
}