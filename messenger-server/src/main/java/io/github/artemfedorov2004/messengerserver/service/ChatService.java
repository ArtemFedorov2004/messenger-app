package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.User;

import java.util.Set;

public interface ChatService {

    Chat createPrivateChat(String participantName1, String participantName2);

    Chat getChat(Long chatId);

    boolean isParticipant(Long chatId, User user);

    Iterable<Chat> getPrivateChats(User user);

    Set<User> getOtherParticipants(Long chatId, User user);

    User getOtherParticipantInPrivateChat(Long chatId, User user);

    boolean existsById(Long chatId);
}
