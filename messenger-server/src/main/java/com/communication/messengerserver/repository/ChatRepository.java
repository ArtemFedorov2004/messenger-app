package com.communication.messengerserver.repository;

import com.communication.messengerserver.entity.Chat;
import com.communication.messengerserver.entity.Message;

import java.util.List;
import java.util.Optional;

public interface ChatRepository {

    List<Chat> findAllByMember(String name);

    boolean exists(String chatId);

    List<String> getMemberNames(String chatId);

    Message pushMessageToChat(String chatId, Message message);

    void updateMessageInChat(String chatId, Message message);

    Optional<Message> findMessageInChat(String chatId, Integer messageId);

    boolean deleteMessage(String chatId, Integer messageId);
}
