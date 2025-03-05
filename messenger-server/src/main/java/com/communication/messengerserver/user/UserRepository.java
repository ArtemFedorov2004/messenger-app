package com.communication.messengerserver.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepository {

    private final MongoTemplate mongoTemplate;

    public List<User> findByUsernameIgnoreCaseContaining(String username) {
        Query query = new Query(Criteria.where("username")
                .regex(".*" + username + ".*", "i"));

        return mongoTemplate.find(query, User.class);
    }

    public Optional<String> getUsernameById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        query.fields().include("username");

        User user = mongoTemplate.findOne(query, User.class);

        if (user == null) {
            return Optional.empty();
        } else {
            return Optional.of(user.getUsername());
        }
    }

    public Optional<User> findById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        User user = mongoTemplate.findOne(query, User.class);

        return Optional.ofNullable(user);
    }

    public User save(User user) {
        return mongoTemplate.save(user);
    }
}
