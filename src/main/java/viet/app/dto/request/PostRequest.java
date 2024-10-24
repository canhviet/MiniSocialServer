package viet.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PostRequest implements Serializable {
    private String title;
    private String image;
    private Long userId;
}
