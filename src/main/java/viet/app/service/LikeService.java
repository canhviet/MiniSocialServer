package viet.app.service;

import viet.app.dto.request.LikeRequest;

public interface LikeService {
    long likePost(LikeRequest res);

    void unLikePost(LikeRequest res);

    boolean check(long userId, long postId);
}
