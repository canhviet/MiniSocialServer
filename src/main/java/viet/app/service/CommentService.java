package viet.app.service;

import viet.app.dto.request.CommentRequest;
import viet.app.dto.response.ResponseData;

public interface CommentService {
    long saveComment(CommentRequest commentRequest);
    ResponseData<?> getAllCommentsByPostId(long postId);
}
