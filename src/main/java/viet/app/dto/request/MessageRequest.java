package viet.app.dto.request;

import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
public class MessageRequest implements Serializable {
    private long senderId;
    private long receiverId;
    private String content;
    private long conversation_id;
    private Date timestamp;
}
