package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.controller.payload.NewMessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.UpdateMessagePayload;
import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MessageService {

    Message createMessage(Chat chat, User sender, NewMessagePayload payload);

    Page<Message> getByChatId(Long chatId, Pageable pageable);

    Message updateMessage(Long messageId, UpdateMessagePayload payload);

    Message getMessage(Long messageId);

    void deleteMessage(Long id);

    boolean canGetMessage(Long messageId, User principal);

    boolean canEditMessage(Long messageId, User principal);

    boolean canDeleteMessage(Long messageId, User principal);

    Optional<Message> findLastMessageByChatId(Long chatId);
}
