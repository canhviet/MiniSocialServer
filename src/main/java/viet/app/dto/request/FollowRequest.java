package viet.app.dto.request;

import lombok.Getter;

@Getter
public class FollowRequest {
    private long userId;
    private long followingId;
}
