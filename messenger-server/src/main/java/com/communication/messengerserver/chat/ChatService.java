package com.communication.messengerserver.chat;

import com.communication.messengerserver.user.User;
import com.communication.messengerserver.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final UserService userService;

    private final ChatRepository chatRepository;

    private String getChatTitle(Chat chat, String userId) {
        User friend;
        if (chat.getUser1Id().equals(userId)) {
            friend = userService.getUserById(chat.getUser2Id());
        } else {
            friend = userService.getUserById(chat.getUser1Id());
        }

        return friend.getUsername();
    }

    public List<Chat> getUserChats(String userId) {
        List<Chat> chats = chatRepository.findAllChatsForUser(userId);

        chats.forEach(chat -> {
            String title = getChatTitle(chat, userId);

            chat.setTitle(title);
        });

        return chats;
    }
}
