package viet.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import viet.app.dto.request.MessageRequest;
import viet.app.service.MessageSocketService;

import java.util.Map;

/**
 * Controller class that handles real-time messaging using WebSocket communication.
 * Routes:
 * - /user: Send user conversations to a specific user by their user ID through a web socket.
 * - /conv: Send messages of a specific conversation to the connected users through a web socket.
 * - /sendMessage: Save a new message using a web socket.
 * - /deleteConversation: Delete a conversation by its unique conversation ID using a web socket.
 * - /deleteMessage: Delete a message by its unique message ID within a conversation using a web socket.
 */
@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageSocketService socketService;

    /**
     * Send user conversations to a specific user by their user ID through a web socket.
     *
     * @param userId The ID of the user for whom to send conversations.
     */
    @MessageMapping("/user")
    public void sendUserConversationByUserId(long userId) {
        socketService.sendUserConversationByUserId(userId);
    }

    /**
     * Send messages of a specific conversation to the connected users through a web socket.
     *
     * @param conversationId The ID of the conversation for which to send messages.
     */
    @MessageMapping("/conv")
    public void sendMessagesByConversationId(long conversationId) {
        socketService.sendMessagesByConversationId(conversationId);
    }

    /**
     * Save a new message using a web socket.
     *
     * @param message The MessageRequest object containing the message details to be saved.
     */
    @MessageMapping("/sendMessage")
    public void saveMessage(MessageRequest message) {
        socketService.saveMessage(message);

    }

    /**
     * Delete a conversation by its unique conversation ID using a web socket.
     *
     * @param payload A Map containing the conversationId, user1Id, and user2Id for the
     *                conversation to be deleted and notify listener.
     */
    @MessageMapping("/deleteConversation")
    public void deleteConversation(Map<String, Object> payload) {
        // Safely convert the conversationId, user1Id, and user2Id to long
        Number conversationIdNumber = (Number) payload.get("conversationId");
        long conversationId = conversationIdNumber.longValue();

        Number user1IdNumber = (Number) payload.get("user1Id");
        long user1Id = user1IdNumber.longValue();

        Number user2IdNumber = (Number) payload.get("user2Id");
        long user2Id = user2IdNumber.longValue();

        // Delete the conversation
        socketService.deleteConversationByConversationId(conversationId);

        // Send updates to the users
        socketService.sendUserConversationByUserId(user1Id);
        socketService.sendUserConversationByUserId(user2Id);
    }


    /**
     * Delete a message by its unique message ID within a conversation using a web socket.
     *
     * @param payload A Map containing the conversationId and messageId for the message
     *                to be deleted and notify listener.
     */
    @MessageMapping("/deleteMessage")
    public void deleteMessage(Map<String, Object> payload) {
        long conversationId = (long) payload.get("conversationId");
        long messageId = (long) payload.get("messageId");
        socketService.deleteMessageByMessageId(conversationId, messageId);
    }
}
