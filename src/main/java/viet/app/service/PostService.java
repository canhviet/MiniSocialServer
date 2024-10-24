package viet.app.service;

import viet.app.dto.request.PostRequest;
import viet.app.dto.response.ResponseData;
import viet.app.model.Post;

public interface PostService {
    long savePost(Post post);
    long savePost(PostRequest postRequest);
    ResponseData<?> getAllPosts();
    ResponseData<?> getAllPostsAndSortByUser(long userId);
    void updatePost(long postId, PostRequest postRequest);
    ResponseData<?> getPostsByUserId(long userId);
}
