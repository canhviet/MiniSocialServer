package viet.app.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import viet.app.dto.request.MessageRequest;
import viet.app.dto.response.ConversationResponse;
import viet.app.dto.response.MessageResponse;
import viet.app.dto.response.WebSocketResponse;
import viet.app.model.Conversation;
import viet.app.model.Message;
import viet.app.model.User;
import viet.app.repository.ConversationRepository;
import viet.app.repository.MessageRepository;
import viet.app.repository.UserRepository;
import viet.app.service.MessageSocketService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageSocketServiceImpl implements MessageSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    /**
     * Send user conversations to a specific user by their user ID through a web socket.
     *
     * @param userId The ID of the user for whom to send conversations.
     */
    @Override
    public void sendUserConversationByUserId(long userId) {
        List<ConversationResponse> conversation = conversationRepository.findConversationsByUserId(userId);
        messagingTemplate.convertAndSend(
                "/topic/user/".concat(String.valueOf(userId)),
                WebSocketResponse.builder()
                        .type("ALL")
                        .data(conversation)
                        .build()
        );
    }

    /**
     * Send messages of a specific conversation to the connected users through a web socket.
     *
     * @param conversationId The ID of the conversation for which to send messages.
     */
    @Override
    public void sendMessagesByConversationId(long conversationId) {
        Conversation conversation = new Conversation();
        conversation.setId(conversationId);
        List<Message> messageList = messageRepository.findAllByConversationId(conversation);
        List<MessageResponse> messageResponseList = messageList.stream()
                .map((message -> MessageResponse.builder()
                        .id(message.getId())
                        .content(message.getContent())
                        .timestamp(message.getTimestamp())
                        .senderId(message.getSenderId().getId())
                        .receiverId(message.getReceiverId().getId())
                        .build())
                ).toList();
        messagingTemplate.convertAndSend("/topic/conv/".concat(String.valueOf(conversationId)), WebSocketResponse.builder()
                .type("ALL")
                .data(messageResponseList)
                .build()
        );
    }

    /**
     * Save a new message using a web socket.
     *
     * @param msg The MessageRequest object containing the message details to be saved.
     */
    @Override
    public void saveMessage(MessageRequest msg) {
        User sender = userRepository.findById(msg.getSenderId()).orElse(null);
        User receiver = userRepository.findById(msg.getReceiverId()).orElse(null);
        Conversation conversation = conversationRepository.findConversationByUsers(sender, receiver);
        Message newMessage = new Message();
        newMessage.setContent(msg.getContent());
        newMessage.setTimestamp(msg.getTimestamp());
        newMessage.setConversationId(conversation);
        newMessage.setSenderId(sender);
        newMessage.setReceiverId(receiver);
        Message savedMessage = messageRepository.save(newMessage);
        // notify listener
        MessageResponse res = MessageResponse.builder()
                .id(savedMessage.getId())
                .content(savedMessage.getContent())
                .timestamp(savedMessage.getTimestamp())
                .senderId(savedMessage.getSenderId().getId())
                .receiverId(savedMessage.getReceiverId().getId())
                .build();
        messagingTemplate.convertAndSend("/topic/conv/".concat(msg.getConversation_id() + ""),
                WebSocketResponse.builder()
                        .type("ADDED")
                        .data(res)
                        .build()
        );
        sendUserConversationByUserId(msg.getSenderId());
        sendUserConversationByUserId(msg.getReceiverId());
        sendMessagesByConversationId(conversation.getId());
    }

    /**
     * Delete a conversation by its unique conversation ID using a web socket.
     *
     * @param conversationId The ID of the conversation to be deleted.
     */
    @Transactional
    @Override
    public void deleteConversationByConversationId(long conversationId) {
        Conversation c = new Conversation();
        c.setId(conversationId);
        messageRepository.deleteAllByConversationId(c);
        conversationRepository.deleteById(conversationId);
    }

    /**
     * Delete a message by its unique message ID within a conversation using a web socket.
     *
     * @param conversationId The ID of the conversation to notify its listener.
     * @param messageId      The ID of the message to be deleted.
     */
    @Override
    public void deleteMessageByMessageId(long conversationId, long messageId) {
        messageRepository.deleteById(messageId);
        // notify listener
        sendMessagesByConversationId(conversationId);
    }
}
