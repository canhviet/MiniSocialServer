package viet.app.service;

import viet.app.dto.request.FollowRequest;
import viet.app.model.User;

public interface FollowService {
    long follow(FollowRequest request);
    void unfollow(FollowRequest request);
    boolean isFollowing(long userId, long followingId);
}
