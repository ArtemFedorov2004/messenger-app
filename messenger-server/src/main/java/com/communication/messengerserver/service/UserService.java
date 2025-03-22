package com.communication.messengerserver.service;

import com.communication.messengerserver.controller.payload.EditUserPayload;
import com.communication.messengerserver.entity.User;

public interface UserService {

    User findUser(String userId);

    User createUser(String firstname, String lastname, String username, String email);

    void editUser(String userId, EditUserPayload payload);
}
