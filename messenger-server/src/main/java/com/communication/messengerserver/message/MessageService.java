package com.communication.messengerserver.message;

import com.communication.messengerserver.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message editMessage(Integer id, Message updatedMessage) {
        Optional<Message> optionalMessage = messageRepository.findById(id);

        if (optionalMessage.isEmpty()) {
            throw new ResourceNotFoundException("Message with id " + id + "not found");
        }

        Message message = optionalMessage.get();

        message.setContent(updatedMessage.getContent());
        message.setCreatedAt(updatedMessage.getCreatedAt());

        messageRepository.save(message);

        return message;
    }
}
