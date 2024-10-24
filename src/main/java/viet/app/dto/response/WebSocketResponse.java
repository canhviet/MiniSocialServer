package viet.app.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
public class WebSocketResponse implements Serializable {
    private String type;
    private Object data;
}
