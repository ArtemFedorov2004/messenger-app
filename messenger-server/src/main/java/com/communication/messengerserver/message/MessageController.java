package com.communication.messengerserver.message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/*
@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        Message savedMessage = messageService.sendMessage(message);
        return ResponseEntity.created(
                        UriComponentsBuilder.fromPath("/messages/{id}")
                                .buildAndExpand(savedMessage.getId())
                                .toUri())
                .body(savedMessage);
    }

    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Message> editMessage(@PathVariable Integer id, @RequestBody Message updatedMessage) {
        Message message = messageService.editMessage(id, updatedMessage);

        return ResponseEntity.ok(message);
    }
}*/
