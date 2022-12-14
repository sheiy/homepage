package site.ownw.homepage.controller.user.model;

import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignInRequest {

    @Parameter(required = true)
    @Pattern(regexp = "[a-zA-Z0-9_-]{5,16}")
    private String nickname;

    @Parameter(required = true)
    @Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}")
    private String password;
}
