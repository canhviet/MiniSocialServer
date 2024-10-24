package viet.app.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {
    private long id;
    private String content;
    private long userId;
    private long postId;
    private String authorName;
    private String authorAvatarUrl;
    private long parentId;
}
