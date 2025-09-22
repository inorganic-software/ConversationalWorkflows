package com.inorganic.ui;

import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.inorganic.workflows.ChatbotService;

import io.quarkus.oidc.UserInfo;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnError;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;

@WebSocket(path = "/chatbot")
public class ChatBotWebSocket {
    
    private static final Logger LOG = Logger.getLogger(ChatBotWebSocket.class);

    @Inject
    ChatbotService chatbotService;

    @Inject
    UserInfo userInfo;

    @OnOpen
    public String onOpen() {
        LOG.info("[ChatBotWebSocket] - onOpen | WebSocket connection opened");
        String greeting = "Hello! I’m MasOrange’s virtual assistant. How can I help you today?";
        return greeting;
    }

    @OnTextMessage
    public void onMessage(String message, WebSocketConnection connection) {
        try {
            String userEmail = getUserEmail();
            LOG.info("[ChatBotWebSocket] - onMessage | Received message from user: " + userEmail);
            String response = chatbotService.processMessage(userEmail, message);
            connection.sendText(response)
                .subscribe().with(
                    success -> LOG.info("[ChatBotWebSocket] - onMessage | Response sent successfully to user: " + userEmail),
                    failure -> LOG.error("[ChatBotWebSocket] - onMessage | FAILED to send response to user: " + userEmail, failure)
                );

        } catch (Exception e) {
            LOG.error("[ChatBotWebSocket] - onMessage | Error processing message", e);
            try {
                connection.sendText("Error processing your request. Please try again.");
            } catch (Exception sendError) {
                LOG.error("[ChatBotWebSocket] - onMessage | Failed to send error message", sendError);
            }
        }
    }

    private String getUserEmail() {
        try {
            return userInfo != null && userInfo.getEmail() != null ? userInfo.getEmail() : "anonymous";
        } catch (Exception e) {
            LOG.warn("[ChatBotWebSocket] - getUserEmail | Failed to get user email, using anonymous", e);
            return "anonymous";
        }
    }

    @OnClose
    public void onClose() {
        LOG.info("[ChatBotWebSocket] - onClose | WebSocket connection closed");
    }

    @OnError
    public void onError(Throwable error) {
        LOG.error("[ChatBotWebSocket] - onError | WebSocket error occurred", error);
    }
}
