package viet.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse implements Serializable {
    private long id;
    private String content;
    private long senderId;
    private long receiverId;
    private Date timestamp;

}
