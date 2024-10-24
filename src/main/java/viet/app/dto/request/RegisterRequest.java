package viet.app.dto.request;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class RegisterRequest implements Serializable {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
}
