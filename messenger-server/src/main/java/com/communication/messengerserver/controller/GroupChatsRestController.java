package com.communication.messengerserver.controller;

import com.communication.messengerserver.controller.payload.AddGroupChatMemberPayload;
import com.communication.messengerserver.controller.payload.NewGroupChatPayload;
import com.communication.messengerserver.entity.GroupChat;
import com.communication.messengerserver.entity.Message;
import com.communication.messengerserver.entity.User;
import com.communication.messengerserver.service.GroupChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("messenger-api/group-chats")
@RequiredArgsConstructor
public class GroupChatsRestController {

    private final GroupChatService groupChatService;

    @GetMapping
    public Iterable<GroupChat> findGroupChats() {
        return this.groupChatService.findGroupChats();
    }

    @GetMapping("{chatId}")
    public GroupChat findGroupChat(@PathVariable("chatId") String chatId) {
        return this.groupChatService.findGroupChat(chatId);
    }

    @PostMapping
    public ResponseEntity<?> createGroupChat(@Valid @RequestBody NewGroupChatPayload payload,
                                             BindingResult bindingResult,
                                             UriComponentsBuilder uriComponentsBuilder)
            throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            GroupChat groupChat = this.groupChatService.createGroupChat(payload.title(), payload.memberIds());

            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("messenger-api/group-chats/{id}")
                            .build(Map.of("id", groupChat.getId())))
                    .body(groupChat);
        }
    }

    @DeleteMapping("{chatId}")
    public ResponseEntity<Void> deleteGroupChat(@PathVariable("chatId") String chatId) {
        this.groupChatService.deleteGroupChat(chatId);

        return ResponseEntity.noContent()
                .build();
    }

    @GetMapping("{chatId}/members")
    public Iterable<User> findMembers(@PathVariable("chatId") String chatId) {
        return this.groupChatService.findMembers(chatId);
    }

    @GetMapping("{chatId}/messages")
    public Iterable<Message> findMessages(@PathVariable("chatId") String chatId) {
        return this.groupChatService.findMessages(chatId);
    }

    @PutMapping("{chatId}/members")
    public ResponseEntity<?> addUserToChatMembers(@RequestBody AddGroupChatMemberPayload payload,
                                                  @PathVariable("chatId") String chatId) {
        this.groupChatService.addUserToChatMembers(payload.userId(), chatId);

        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("{chatId}/members/{memberId}")
    public ResponseEntity<?> deleteUserFromChatMembers(@PathVariable("chatId") String chatId,
                                                       @PathVariable("memberId") String memberId) {
        this.groupChatService.deleteChatMember(memberId, chatId);

        return ResponseEntity.noContent()
                .build();
    }
}
