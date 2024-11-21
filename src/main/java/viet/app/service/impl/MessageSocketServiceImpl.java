package viet.app.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import viet.app.dto.request.FollowRequest;
import viet.app.dto.request.MessageRequest;
import viet.app.dto.response.ConversationResponse;
import viet.app.dto.response.MessageResponse;
import viet.app.dto.response.NotificationResponse;
import viet.app.dto.response.WebSocketResponse;
import viet.app.model.Conversation;
import viet.app.model.Message;
import viet.app.model.Notification;
import viet.app.model.User;
import viet.app.repository.ConversationRepository;
import viet.app.repository.MessageRepository;
import viet.app.repository.NotificationRepository;
import viet.app.repository.UserRepository;
import viet.app.service.MessageSocketService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSocketServiceImpl implements MessageSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

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

    @Override
    public void showNotification(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        List<Notification> notificationList = notificationRepository.findAllByUser(user);
        List<NotificationResponse> list = notificationList.stream()
                .map((notification -> NotificationResponse.builder()
                        .id(notification.getId())
                        .content(notification.getContent())
                        .read(notification.isRead())
                        .timestamp(notification.getTimestamp())
                        .isMessage(notification.isMessage())
                        .isFollow(notification.isFollow())
                        .otherUser(notification.getOtherUser().getId())
                        .build()))
                .toList();
        messagingTemplate.convertAndSend(
                "/topic/notify/".concat(String.valueOf(userId)),
                WebSocketResponse.builder()
                        .type("ALL")
                        .data(list)
                        .build()
        );
    }

    @Override
    public void followNotification(FollowRequest followRequest) {
        User followingUser = userRepository.findById(followRequest.getUserId()).orElse(null);
        Notification noti = new Notification();
        noti.setUser(userRepository.findById(followRequest.getFollowingId()).orElse(null));
        noti.setContent(followingUser.getName() + " is following you");
        noti.setRead(false);
        noti.setFollow(true);
        noti.setMessage(false);
        noti.setOtherUser(followingUser);

        Notification saveNotification = notificationRepository.save(noti);

        NotificationResponse notificationResponse = NotificationResponse.builder()
                .id(saveNotification.getId())
                .content(saveNotification.getContent())
                .read(saveNotification.isRead())
                .timestamp(saveNotification.getTimestamp())
                .isFollow(true)
                .isMessage(false)
                .otherUser(saveNotification.getOtherUser().getId())
                .build();

        messagingTemplate.convertAndSend(
                "/topic/notify/".concat(String.valueOf(followRequest.getFollowingId())),
                WebSocketResponse.builder()
                        .type("FOLLOW")
                        .data(notificationResponse)
                        .build()
        );
        showNotification(followRequest.getFollowingId());
    }

    @Override
    public void messageNotification(MessageRequest msg) {
        User senderUser = userRepository.findById(msg.getSenderId()).orElse(null);
        Notification noti = new Notification();
        noti.setUser(userRepository.findById(msg.getReceiverId()).orElse(null));
        noti.setContent(senderUser.getName() + " messages you: " + msg.getContent());
        noti.setRead(false);
        noti.setFollow(false);
        noti.setMessage(true);
        noti.setOtherUser(senderUser);

        Notification saveNotification = notificationRepository.save(noti);

        NotificationResponse notificationResponse = NotificationResponse.builder()
                .id(saveNotification.getId())
                .content(saveNotification.getContent())
                .read(saveNotification.isRead())
                .timestamp(saveNotification.getTimestamp())
                .isMessage(true)
                .isFollow(false)
                .otherUser(saveNotification.getOtherUser().getId())
                .build();

        messagingTemplate.convertAndSend(
                "/topic/notify/".concat(String.valueOf(msg.getReceiverId())),
                WebSocketResponse.builder()
                        .type("MSG")
                        .data(notificationResponse)
                        .build()
        );
        showNotification(msg.getReceiverId());
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
        messageNotification(msg);
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
