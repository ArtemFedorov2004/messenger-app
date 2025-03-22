package com.communication.messengerserver.controller;

import com.communication.messengerserver.controller.payload.EditMessagePayload;
import com.communication.messengerserver.controller.payload.NewMessagePayload;
import com.communication.messengerserver.entity.Message;
import com.communication.messengerserver.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("messenger-api/messages")
public class MessagesRestController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<?> createMessage(@Valid @RequestBody NewMessagePayload payload,
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
            Message message = this.messageService.createMessage(payload.content(), payload.senderId(), payload.chatId());

            return ResponseEntity
                    .created(uriComponentsBuilder
                            .replacePath("messenger-api/messages/{messageId}")
                            .build(Map.of("messageId", message.getId())))
                    .body(message);
        }
    }

    @PatchMapping("{messageId}")
    public ResponseEntity<?> editMessage(@Valid @RequestBody EditMessagePayload payload,
                                         @PathVariable("messageId") String messageId,
                                         BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException(bindingResult);
            }
        } else {
            this.messageService.editMessage(messageId, payload.content());

            return ResponseEntity.noContent()
                    .build();
        }
    }

    @DeleteMapping("{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") String messageId) {
        this.messageService.deleteMessage(messageId);

        return ResponseEntity.noContent()
                .build();
    }
}
