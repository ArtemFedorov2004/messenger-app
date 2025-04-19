package com.communication.messengerserver.service;

import com.communication.messengerserver.entity.MessageNotification;
import com.communication.messengerserver.entity.User;

public interface NotificationService {

    void send(MessageNotification notification);

    // это потом надо убрать
    void send(User newUser);
}
