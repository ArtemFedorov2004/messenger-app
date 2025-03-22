package com.communication.messengerserver.service;

import com.communication.messengerserver.entity.GroupChat;
import com.communication.messengerserver.entity.Message;
import com.communication.messengerserver.entity.User;
import com.communication.messengerserver.repository.GroupChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DefaultGroupChatService implements GroupChatService {

    private final GroupChatRepository groupChatRepository;

    @Override
    public Iterable<GroupChat> findGroupChats() {
        return this.groupChatRepository.findAll();
    }

    @Override
    public GroupChat findGroupChat(String chatId) {
        return this.groupChatRepository.findById(chatId)
                .orElseThrow(() -> new NoSuchElementException("messenger-server.errors.group_chat.not_found"));
    }

    @Override
    public GroupChat createGroupChat(String title, List<String> memberIds) {
        List<User> members = memberIds.stream()
                .map((id) -> User.builder()
                        .id(id)
                        .build())
                .toList();
        return this.groupChatRepository.save(new GroupChat(null, title, members, new LinkedList<>()));
    }

    @Override
    public void deleteGroupChat(String chatId) {
        this.groupChatRepository.deleteById(chatId);
    }

    @Override
    public Iterable<User> findMembers(String chatId) {
        return this.findGroupChat(chatId)
                .getMembers();
    }

    @Override
    public Iterable<Message> findMessages(String chatId) {
        return this.findGroupChat(chatId)
                .getMessages();
    }

    @Override
    public void addUserToChatMembers(String userId, String chatId) {
        this.groupChatRepository.findAndPushUserToMembersById(userId, chatId);
    }

    @Override
    public void deleteChatMember(String memberId, String chatId) {
        this.groupChatRepository.findAndDeleteChatMemberById(memberId, chatId);
    }
}
