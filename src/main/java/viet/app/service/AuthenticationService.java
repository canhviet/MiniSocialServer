package viet.app.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import viet.app.dto.request.RegisterRequest;
import viet.app.dto.request.ResetPasswordDTO;
import viet.app.dto.request.SignInRequest;
import viet.app.dto.request.UserRequestDTO;
import viet.app.dto.response.TokenResponse;
import viet.app.exception.InvalidDataException;
import viet.app.model.*;
import viet.app.repository.RoleRepository;
import viet.app.repository.UserHasRoleRepository;
import viet.app.util.UserType;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.springframework.http.HttpHeaders.REFERER;
import static viet.app.util.TokenType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;
    private final JwtService jwtService;
    private final MailService mailService;
    private final RoleRepository roleRepository;
    private final UserHasRoleRepository userHasRoleRepository;
    private final RedisTokenService redisTokenService;

    public TokenResponse accessToken(SignInRequest signInRequest) {
        log.info("---------- authenticate ----------");

        var user = userService.getByUsername(signInRequest.getUsername());
        if (!user.isEnabled()) {
            throw new InvalidDataException("User not active");
        }

        List<String> roles = userService.getAllRolesByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword(), authorities)
        );

        // create new access token
        String accessToken = jwtService.generateToken(user);

        // create new refresh token
        String refreshToken = jwtService.generateRefreshToken(user);

        // save token to db
        //tokenService.save(Token.builder().username(user.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());
        redisTokenService.save(RedisToken.builder().id(user.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .userType(UserType.USER)
                .build();
    }

    /**
     * Refresh token
     *
     * @param request
     * @return
     */
    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("---------- refreshToken ----------");

        final String refreshToken = request.getHeader(REFERER);
        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }
        final String userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);
        var user = userService.getByUsername(userName);
        if (!jwtService.isValid(refreshToken, REFRESH_TOKEN, user)) {
            throw new InvalidDataException("Not allow access with this token");
        }

        // create new access token
        String accessToken = jwtService.generateToken(user);

        // save token to db
        //tokenService.save(Token.builder().username(user.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());
        redisTokenService.save(RedisToken.builder().id(user.getUsername()).accessToken(accessToken).refreshToken(refreshToken).build());


        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    /**
     * Logout
     *
     * @param request
     * @return
     */
    public String removeToken(HttpServletRequest request) {
        log.info("---------- removeToken ----------");

        final String token = request.getHeader(REFERER);
        if (StringUtils.isBlank(token)) {
            throw new InvalidDataException("Token must be not blank");
        }

        final String userName = jwtService.extractUsername(token, ACCESS_TOKEN);

        //tokenService.delete(userName);
        redisTokenService.remove(userName);

        return "Removed!";
    }

    /**
     * Forgot password
     *
     * @param email
     */
    public String forgotPassword(String email) throws MessagingException, UnsupportedEncodingException {
        log.info("---------- forgotPassword ----------");

        // check email exists or not
        User user = userService.getUserByEmail(email);

        // generate reset token
        String resetToken = jwtService.generateResetToken(user);

        // save to db
        //tokenService.save(Token.builder().username(user.getUsername()).resetToken(resetToken).build());
        redisTokenService.save(RedisToken.builder().id(user.getUsername()).resetToken(resetToken).build());


        // TODO send email to user
        String confirmLink = String.format("http://localhost:4200/reset-password?token=%s", resetToken);

        mailService.sendEmail(email, "forgot-password", "click the link below to change your password " + confirmLink, null);

        return resetToken;
    }

    /**
     * Reset password
     *
     * @param secretKey
     * @return
     */
    public String resetPassword(String secretKey) {
        log.info("---------- resetPassword ----------");

        // validate token
        var user = validateToken(secretKey);

        // check token by username
        tokenService.getByUsername(user.getUsername());

        return "Reset";
    }

    public String changePassword(ResetPasswordDTO request) {
        log.info("---------- changePassword ----------");

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidDataException("Passwords do not match");
        }

        // get user by reset token
        var user = validateToken(request.getSecretKey());

        // update password
        user.setPassword(request.getPassword());
        userService.saveUser(user);

        return "Changed";
    }

    /**
     * Validate user and reset token
     *
     * @param token
     * @return
     */
    private User validateToken(String token) {
        // validate token
        var userName = jwtService.extractUsername(token, RESET_TOKEN);

        // check token in redis
        redisTokenService.isExists(userName);

        // validate user is active or not
        var user = userService.getByUsername(userName);
        if (!user.isEnabled()) {
            throw new InvalidDataException("User not active");
        }

        return user;
    }

    public String register(RegisterRequest res) {
        Role role = roleRepository.findByName("USER").orElse(null);
        User user = User.builder()
                .username(res.getUsername())
                .email(res.getEmail())
                .password(res.getPassword())
                .build();
        userService.saveUser(user);
        UserHasRole userHasRole = new UserHasRole(user, role);
        userHasRoleRepository.save(userHasRole);
        return "register success";
    }
}
