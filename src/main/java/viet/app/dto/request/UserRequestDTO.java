package viet.app.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import viet.app.dto.validator.EnumPattern;
import viet.app.dto.validator.EnumValue;
import viet.app.dto.validator.GenderSubset;
import viet.app.dto.validator.PhoneNumber;
import viet.app.model.UserHasRole;
import viet.app.util.Gender;
import viet.app.util.UserStatus;
import viet.app.util.UserType;

import java.io.Serializable;
import java.util.Date;

import static viet.app.util.Gender.*;

@Getter
public class UserRequestDTO implements Serializable {

    @NotBlank(message = "name must be not blank") // Khong cho phep gia tri blank
    private String name;

    @Email(message = "email invalid format") // Chi chap nhan nhung gia tri dung dinh dang email
    private String email;

    //@Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
    @PhoneNumber(message = "phone invalid format")
    private String phone;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    //@Pattern(regexp = "^male|female|other$", message = "gender must be one in {male, female, other}")
    @GenderSubset(anyOf = {MALE, FEMALE, OTHER})
    private Gender gender;

    @NotNull(message = "username must be not null")
    private String username;

    @NotNull(message = "password must be not null")
    private String password;

    @NotNull(message = "type must be not null")
    @EnumValue(name = "type", enumClass = UserType.class)
    private String type;

    @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;

    private String avatar;

}
