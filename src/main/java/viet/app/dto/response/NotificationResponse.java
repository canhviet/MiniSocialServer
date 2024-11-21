package viet.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse implements Serializable {
    private long id;
    private String content;
    private boolean read;
    private Date timestamp;
    private boolean isFollow;
    private boolean isMessage;
    private long otherUser;
}
