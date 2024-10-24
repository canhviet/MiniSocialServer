package viet.app.dto.response;

import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

public interface ConversationResponse {
    long getConversationId();
    long getOtherUserId();
    String getOtherUserName();
    String getLastMessage();
    Date getLastMessageTimestamp();
    String getUserAvatar();
}
