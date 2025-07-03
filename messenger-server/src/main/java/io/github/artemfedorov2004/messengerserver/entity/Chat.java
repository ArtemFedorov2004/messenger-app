package io.github.artemfedorov2004.messengerserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity
@Table(schema = "messenger", name = "t_chat")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "t_chat_participant",
            schema = "messenger",
            joinColumns = @JoinColumn(name = "id_chat"),
            inverseJoinColumns = @JoinColumn(name = "username")
    )
    private Set<User> participants;
}
