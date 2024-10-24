package viet.app.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentRequest {
    private long postId;
    private long userId;
    private String content;
    private long parentId;
}
