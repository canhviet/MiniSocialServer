package viet.app.service;

import viet.app.dto.request.FollowRequest;
import viet.app.dto.request.MessageRequest;

public interface MessageSocketService {
    /**
     * Send user conversations to a specific user by their user ID through a web socket.
     *
     * @param userId The ID of the user for whom to send conversations.
     */
    void sendUserConversationByUserId(long userId);

    /**
     * Send messages of a specific conversation to the connected users through a web socket.
     *
     * @param conversationId The ID of the conversation for which to send messages.
     */
    void sendMessagesByConversationId(long conversationId);

    /**
     * Save a new message using a web socket.
     *
     * @param msg The MessageRequest object containing the message details to be saved.
     */
    void saveMessage(MessageRequest msg);

    /**
     * Delete a conversation by its unique conversation ID using a web socket.
     *
     * @param conversationId The ID of the conversation to be deleted.
     */
    void deleteConversationByConversationId(long conversationId);

    /**
     * Delete a message by its unique message ID within a conversation using a web socket.
     *
     * @param conversationId The ID of the conversation to notify its listener.
     * @param messageId      The ID of the message to be deleted.
     */
    void deleteMessageByMessageId(long conversationId, long messageId);

    void showNotification(long userId);

    void followNotification(FollowRequest followRequest);

    void messageNotification(MessageRequest msg);
}
