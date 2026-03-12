package com.example.chat.chat;

import lombok.AllArgsConstructor;

public record ChatMessages(
        String content,

        String sender,

        MessageType type
) {
}
