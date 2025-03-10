package com.communication.messengerserver.chat.friendmessage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class FriendMessageService {

    private final FriendMessageRepository friendMessageRepository;

    public FriendMessage saveMessage(FriendMessage message) {
        return friendMessageRepository.save(message);
    }

    public List<FriendMessage> getMessagesByIds(List<String> ids) {
        return friendMessageRepository.findAllByIdIn(ids);
    }
}
