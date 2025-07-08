package io.github.artemfedorov2004.messengerserver.service;

import io.github.artemfedorov2004.messengerserver.controller.payload.NewMessagePayload;
import io.github.artemfedorov2004.messengerserver.controller.payload.UpdateMessagePayload;
import io.github.artemfedorov2004.messengerserver.entity.Chat;
import io.github.artemfedorov2004.messengerserver.entity.Message;
import io.github.artemfedorov2004.messengerserver.entity.User;
import io.github.artemfedorov2004.messengerserver.exception.ResourceNotFoundException;
import io.github.artemfedorov2004.messengerserver.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DefaultMessageService implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public Message createMessage(Chat chat, User sender, NewMessagePayload payload) {
        Message message = new Message(null, chat, sender, payload.content(), LocalDateTime.now(), null);
        return this.messageRepository.save(message);
    }

    @Override
    public Page<Message> getByChatId(Long chatId, Pageable pageable) {
        return this.messageRepository.findByChatId(chatId, pageable);
    }

    @Override
    @Transactional
    public Message updateMessage(Long messageId, UpdateMessagePayload payload) {
        Message message = this.getMessage(messageId);

        message.setContent(payload.content());
        message.setEditedAt(LocalDateTime.now());

        return message;
    }

    @Override
    public Message getMessage(Long messageId) {
        return this.messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("messenger_server.errors.message.not_found"));
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        this.messageRepository.deleteById(id);
    }

    @Override
    public boolean canGetMessage(Long messageId, User principal) {
        return this.messageRepository.existsByIdAndChatParticipantsContaining(messageId, principal);
    }

    @Override
    public boolean canEditMessage(Long messageId, User principal) {
        return this.messageRepository.existsByIdAndSender(messageId, principal);
    }

    @Override
    public boolean canDeleteMessage(Long messageId, User principal) {
        return this.messageRepository.existsByIdAndSender(messageId, principal);
    }

    @Override
    public Optional<Message> findLastMessageByChatId(Long chatId) {
        return this.messageRepository.findLastMessageByChatId(chatId);
    }
}
