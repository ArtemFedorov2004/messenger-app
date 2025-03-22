package com.communication.messengerserver.service;

import com.communication.messengerserver.controller.mapper.UserMapper;
import com.communication.messengerserver.controller.payload.EditUserPayload;
import com.communication.messengerserver.entity.User;
import com.communication.messengerserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findUser(String userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("messenger-server.errors.group_chat.not_found"));
    }

    @Override
    public User createUser(String firstname, String lastname, String username, String email) {
        return this.userRepository.save(User.builder()
                .firstname(firstname)
                .lastname(lastname)
                .username(username)
                .email(email)
                .build());
    }

    @Override
    public void editUser(String userId, EditUserPayload payload) {
        User editedUser = UserMapper.toUser(payload);
        this.userRepository.findById(userId)
                .ifPresentOrElse(user -> {
                    Field[] fields = User.class.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        try {
                            Object fieldValue = field.get(editedUser);
                            if (fieldValue != null) {
                                ReflectionUtils.setField(field, user, fieldValue);
                            }
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    this.userRepository.save(user);
                }, () -> {
                    throw new NoSuchElementException("messenger-server.errors.group_chat.not_found");
                });
    }
}
