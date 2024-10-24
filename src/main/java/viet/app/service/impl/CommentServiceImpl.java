package viet.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import viet.app.dto.request.CommentRequest;
import viet.app.dto.response.CommentResponse;
import viet.app.dto.response.ResponseData;
import viet.app.exception.ResourceNotFoundException;
import viet.app.model.Comment;
import viet.app.model.Post;
import viet.app.model.User;
import viet.app.repository.CommentRepository;
import viet.app.repository.PostRepository;
import viet.app.repository.UserRepository;
import viet.app.service.CommentService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    @Override
    public long saveComment(CommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPost(getPostById(commentRequest.getPostId()));
        comment.setUser(getUserById(commentRequest.getUserId()));
        commentRepository.save(comment);

        log.info("Saving comment {}", comment.getContent());

        return comment.getId();
    }

    @Override
    public ResponseData<?> getAllCommentsByPostId(long postId) {
        List<Comment> list = commentRepository.findAllByPostId(postId);
        List<CommentResponse> comments = list.stream().map(comment -> CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .authorName(comment.getUser().getName())
                .authorAvatarUrl(comment.getUser().getAvatar())
                .build()).toList();
        return new ResponseData<>(HttpStatus.OK.value(), "get all comments", comments);
    }

    private Post getPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("post.not.found"));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user.not.found"));
    }
}
