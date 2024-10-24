package viet.app.dto.response;

import lombok.Builder;
import lombok.Getter;
import viet.app.util.Gender;
import viet.app.util.UserStatus;
import viet.app.util.UserType;

import java.io.Serializable;
import java.util.Date;

@Builder
@Getter
public class UserDetailResponse implements Serializable {
    private long id;

    private String name;

    private String email;

    private String phone;

    private Date dateOfBirth;

    private Gender gender;

    private String username;

    private UserType type;

    private UserStatus status;

    private String avatar;
}
