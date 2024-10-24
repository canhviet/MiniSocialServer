package viet.app.service.impl;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import viet.app.dto.request.FollowRequest;
import viet.app.exception.ResourceNotFoundException;
import viet.app.model.Follow;
import viet.app.model.User;
import viet.app.repository.FollowRepository;
import viet.app.repository.UserRepository;
import viet.app.service.FollowService;

@Service
@Slf4j
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    @Override
    public long follow(FollowRequest followRequest) {
        Follow follow = Follow.builder()
                .followingId(userRepository.findById(followRequest.getFollowingId()).orElse(null))
                .userId(userRepository.findById(followRequest.getUserId()).orElse(null))
                .build();
        followRepository.save(follow);
        log.info("Following {} to {}", followRequest.getFollowingId(), followRequest.getUserId());
        return follow.getId();
    }

    @Override
    public void unfollow(FollowRequest followRequest) {
        User followingUser = userRepository.findById(followRequest.getFollowingId()).orElse(null);
        User user = userRepository.findById(followRequest.getUserId()).orElse(null);
        followRepository.unFollowUser(followingUser, user);
        log.info("unFollow {} to {}", followRequest.getFollowingId(), followRequest.getUserId());
    }

    @Override
    public boolean isFollowing(long userId, long followingId) {
        User followingUser = userRepository.findById(followingId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        return followRepository.existsByFollowingIdAndUserId(followingUser, user);
    }
}
