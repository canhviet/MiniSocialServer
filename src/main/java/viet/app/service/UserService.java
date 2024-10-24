package viet.app.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import viet.app.dto.request.UserRequestDTO;
import viet.app.dto.response.PageResponse;
import viet.app.dto.response.ResponseData;
import viet.app.dto.response.UserDetailResponse;
import viet.app.model.User;
import viet.app.util.UserStatus;

import java.util.List;

public interface UserService {

    UserDetailsService userDetailsService();

    User getByUsername(String userName);

    long saveUser(UserRequestDTO request);

    long saveUser(User user);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsers(int pageNo, int pageSize);

    List<String> getAllRolesByUserId(long userId);

    User getUserByEmail(String email);

    User getOrCreateUser(OAuth2User oAuth2User);

    ResponseData<?> findAllUsersExceptThisUserId(long userId);

    ResponseData<?> searchUser(String s, long userId);

    ResponseData<?> findConversationIdByUser1IdAndUser2Id(long user1Id, long user2Id);

}
