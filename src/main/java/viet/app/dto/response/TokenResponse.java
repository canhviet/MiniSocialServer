package viet.app.dto.response;

import lombok.Builder;
import lombok.Getter;
import viet.app.model.Role;
import viet.app.util.UserType;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
public class TokenResponse implements Serializable {

    private String accessToken;

    private String refreshToken;

    private Long userId;

    private UserType userType;

    // more over
}
