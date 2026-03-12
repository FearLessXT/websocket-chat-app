package com.example.chat.config;

import com.example.chat.chat.ChatMessages;
import com.example.chat.chat.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.getSessionAttributes() == null) {
            log.info("No session attributes found in disconnect event:");
            return;
        }

        String username = headerAccessor.getSessionAttributes().get("username").toString();
        if (username != null) {
            log.info("User disconnected: {}", username);
            ChatMessages data = new ChatMessages(
                    "",
              username,
              MessageType.LEAVE
            );
            messagingTemplate.convertAndSend("/topic/public", data);
        }
    }
}
