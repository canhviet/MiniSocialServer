package viet.app.service.impl;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import viet.app.dto.request.LikeRequest;
import viet.app.model.Like;
import viet.app.model.Post;
import viet.app.model.User;
import viet.app.repository.LikeRepository;
import viet.app.repository.PostRepository;
import viet.app.repository.UserRepository;
import viet.app.service.LikeService;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public long likePost(LikeRequest res) {
        Like like = Like.builder()
                .post(postRepository.findById(res.getPostId()).orElse(null))
                .user(userRepository.findById(res.getUserId()).orElse(null))
                .build();
        likeRepository.save(like);
        log.info("Like {} to {}", res.getPostId(), res.getUserId());
        return like.getId();
    }

    @Override
    public void unLikePost(LikeRequest res) {
        Post post = postRepository.findById(res.getPostId()).orElse(null);
        User user = userRepository.findById(res.getUserId()).orElse(null);
        likeRepository.unLikePost(user, post);
        log.info("unlike {} to {}", res.getPostId(), res.getUserId());
    }

    @Override
    public boolean check(long userId, long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        return likeRepository.existsByUserAndPost(user, post);
    }
}
