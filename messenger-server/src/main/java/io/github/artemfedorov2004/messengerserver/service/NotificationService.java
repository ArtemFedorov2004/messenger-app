package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.entity.*;

import java.util.List;

public interface NotificationService {

    void send(User sender, Chat chat, PrivateChatNotification.Type notificationType);

    void send(User sender, Message message, MessageNotification.Type notificationType);
}
