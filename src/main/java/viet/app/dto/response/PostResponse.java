package viet.app.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String image;
    private long userId;
}
