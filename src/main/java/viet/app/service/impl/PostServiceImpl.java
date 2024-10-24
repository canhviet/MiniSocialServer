package viet.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import viet.app.dto.request.PostRequest;
import viet.app.dto.response.PostResponse;
import viet.app.dto.response.ResponseData;
import viet.app.exception.ResourceNotFoundException;
import viet.app.model.Post;
import viet.app.model.User;
import viet.app.repository.LikeRepository;
import viet.app.repository.PostRepository;
import viet.app.repository.UserRepository;
import viet.app.service.PostService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public long savePost(Post post) {
        postRepository.save(post);
        return post.getId();
    }

    @Override
    public long savePost(PostRequest postRequest) {
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .image(postRequest.getImage())
                .user(userRepository.findById(postRequest.getUserId()).orElse(null))
                .build();
        postRepository.save(post);

        log.info("Saved post: {}", post.getTitle());
        return post.getId();
    }

    @Override
    public ResponseData<?> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostResponse> list = posts.stream().map(post -> PostResponse.builder()
                .id(post.getId())
                .image(post.getImage())
                .userId(post.getUser().getId())
                .title(post.getTitle())
                .build()).toList();
        return new ResponseData<>(HttpStatus.OK.value(), "get all posts", list);
    }

    @Override
    public ResponseData<?> getAllPostsAndSortByUser(long userId) {
        List<Post> posts = postRepository.getPostsAndSortByUser(userId);
        List<PostResponse> list = posts.stream().map(post -> PostResponse.builder()
                .id(post.getId())
                .image(post.getImage())
                .userId(post.getUser().getId())
                .title(post.getTitle())
                .build()).toList();
        return new ResponseData<>(HttpStatus.OK.value(), "get all posts", list);
    }

    @Override
    public void updatePost(long postId, PostRequest postRequest) {
        Post post = getPostById(postId);
        post.setTitle(postRequest.getTitle());
        post.setImage(postRequest.getImage());
        postRepository.save(post);
        log.info("Updated post: {}", post.getTitle());
    }

    @Override
    public ResponseData<?> getPostsByUserId(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        List<Post> posts = postRepository.getPostsByUser(user);

        List<PostResponse> list = posts.stream().map(post -> PostResponse.builder()
                .id(post.getId())
                .image(post.getImage())
                .userId(post.getUser().getId())
                .title(post.getTitle())
                .build()).toList();
        return new ResponseData<>(HttpStatus.OK.value(), "get all posts by userId", list);
    }

    /**
     * Get post by postId
     *
     * @param postId
     * @return Post
     */
    private Post getPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("post.not.found"));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user.not.found"));
    }
}
