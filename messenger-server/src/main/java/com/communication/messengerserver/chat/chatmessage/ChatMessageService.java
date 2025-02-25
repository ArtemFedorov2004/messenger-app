package com.communication.messengerserver.chat.chatmessage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(String chatId, ChatMessage chatMessage) {
        return chatMessageRepository.save(chatId, chatMessage);
    }
}
