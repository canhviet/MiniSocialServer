package viet.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import viet.app.dto.request.UserRequestDTO;
import viet.app.dto.response.PageResponse;
import viet.app.dto.response.ResponseData;
import viet.app.dto.response.ResponseError;
import viet.app.dto.response.UserDetailResponse;
import viet.app.exception.ResourceNotFoundException;
import viet.app.model.Conversation;
import viet.app.model.Role;
import viet.app.model.User;
import viet.app.model.UserHasRole;
import viet.app.repository.*;
import viet.app.service.UserService;
import viet.app.util.UserStatus;
import viet.app.util.UserType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final RoleRepository roleRepository;
    private final UserHasRoleRepository userHasRoleRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User getByUsername(String userName) {
        return userRepository.findByUsername(userName).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Save new user to DB
     *
     * @param request
     * @return userId
     */
    @Override
    public long saveUser(UserRequestDTO request) {
        Role role = roleRepository.findByName("USER").orElse(null);

        User user = User.builder()
                .name(request.getName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .avatar(request.getAvatar())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .build();
        userRepository.save(user);

        if(!userHasRoleRepository.existsByUserAndRole(user, role)) {
            UserHasRole userHasRole = new UserHasRole(user, role);
            userHasRoleRepository.save(userHasRole);
        }

        log.info("User has added successfully, userId={}", user.getId());

        return user.getId();
    }

    @Override
    public long saveUser(User user) {
        userRepository.save(user);
        return user.getId();
    }

    /**
     * Update user by userId
     *
     * @param userId
     * @param request
     */
    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        User user = getUserById(userId);
        user.setName(request.getName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        if (!request.getEmail().equals(user.getEmail())) {
            // check email from database if not exist then allow update email otherwise throw exception
            user.setEmail(request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setAvatar(request.getAvatar());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        userRepository.save(user);

        log.info("User has updated successfully, userId={}", userId);
    }

    /**
     * Change status of user by userId
     *
     * @param userId
     * @param status
     */
    @Override
    public void changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);

        log.info("User status has changed successfully, userId={}", userId);
    }

    /**
     * Delete user by userId
     *
     * @param userId
     */
    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
        log.info("User has deleted permanent successfully, userId={}", userId);
    }

    /**
     * Get user detail by userId
     *
     * @param userId
     * @return
     */
    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        return UserDetailResponse.builder()
                .id(userId)
                .name(user.getName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .email(user.getEmail())
                .username(user.getUsername())
                .status(user.getStatus())
                .type(user.getType())
                .build();
    }

    /**
     * Get all user per pageNo and pageSize
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageResponse<?> getAllUsers(int pageNo, int pageSize) {
        Page<User> page = userRepository.findAll(PageRequest.of(pageNo, pageSize));

        List<UserDetailResponse> list = page.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .avatar(user.getAvatar())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .status(user.getStatus())
                        .type(user.getType())
                        .build())
                .toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public List<String> getAllRolesByUserId(long userId) {
        return userRepository.findAllRolesByUserId(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Email not found"));
    }

    @Override
    public User getOrCreateUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");

        User user = getUserByEmail(email);

        if (user == null) {
            Role role = roleRepository.findByName("USER").orElse(null);
            user = new User();
            user.setEmail(email);
            user.setUsername(email);
            user.setPassword(generateRandomPassword());
            user.setName(oAuth2User.getAttribute("name"));
            user.setAvatar(oAuth2User.getAttribute("picture"));
            userRepository.save(user);
            UserHasRole userHasRole = new UserHasRole(user, role);
            userHasRoleRepository.save(userHasRole);
        }
        return user;
    }

    public String generateRandomPassword() {
        return UUID.randomUUID().toString();
    }

    @Override
    public ResponseData<?> findAllUsersExceptThisUserId(long userId) {
        List<User> users = userRepository.findAllUsersExceptThisUserId(userId);
        List<UserDetailResponse> list = users.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .avatar(user.getAvatar())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .status(user.getStatus())
                        .type(user.getType())
                        .build())
                .toList();
        return new ResponseData<>(HttpStatus.OK.value(), "success", list);
    }

    @Override
    public ResponseData<?> searchUser(String s, long userId) {
        List<User> users = userRepository.searchUser(s, userId);
        List<UserDetailResponse> list = users.stream().map(user -> UserDetailResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .dateOfBirth(user.getDateOfBirth())
                        .gender(user.getGender())
                        .avatar(user.getAvatar())
                        .phone(user.getPhone())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .status(user.getStatus())
                        .type(user.getType())
                        .build())
                .toList();
        return new ResponseData<>(HttpStatus.OK.value(), "success", list);
    }

    @Override
    public ResponseData<?> findConversationIdByUser1IdAndUser2Id(long user1Id, long user2Id) {
        long conversationId;
        User user1 = userRepository.findById(user1Id).orElse(null);
        User user2 = userRepository.findById(user2Id).orElse(null);

        if(user1 == null || user2 == null) {
            return new ResponseError(HttpStatus.NOT_FOUND.value(), "user 1 or user 2 is null");
        }

        Conversation existingConversation = conversationRepository.findConversationByUsers(user1, user2);
        if (existingConversation != null) {
            conversationId = existingConversation.getId();
        } else {
            Conversation newConversation = new Conversation();
            newConversation.setUser1(user1);
            newConversation.setUser2(user2);
            Conversation savedConversation = conversationRepository.save(newConversation);
            conversationId = savedConversation.getId();
        }
        return new ResponseData<>(HttpStatus.OK.value(), "success", conversationId);
    }

    /**
     * Get user by userId
     *
     * @param userId
     * @return User
     */
    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user.not.found"));
    }
}
