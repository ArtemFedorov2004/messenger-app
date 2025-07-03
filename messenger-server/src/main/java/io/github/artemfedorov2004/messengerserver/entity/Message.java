package io.github.artemfedorov2004.messengerserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(schema = "messenger", name = "t_message")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinTable(
            name = "t_chat_message",
            schema = "messenger",
            joinColumns = @JoinColumn(
                    name = "id_message"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "id_chat"
            )
    )
    private Chat chat;

    @ManyToOne
    @JoinTable(
            name = "t_message_sender",
            schema = "messenger",
            joinColumns = @JoinColumn(
                    name = "id_message"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "username"
            )
    )
    private User sender;

    @Column(name = "c_content")
    private String content;

    @Column(name = "c_created_at")
    private LocalDateTime createdAt;

    @Column(name = "c_edited_at")
    private LocalDateTime editedAt;
}
