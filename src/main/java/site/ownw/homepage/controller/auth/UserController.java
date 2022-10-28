package site.ownw.homepage.controller.auth;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.ownw.homepage.controller.auth.model.PatchUserRequest;
import site.ownw.homepage.controller.auth.model.SignInRequest;
import site.ownw.homepage.controller.auth.model.SignUpRequest;
import site.ownw.homepage.domain.user.AuthService;
import site.ownw.homepage.domain.user.UserService;
import site.ownw.homepage.entity.User;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/api/v1/users:signIn")
    public String signIn(
            @Valid @RequestBody SignInRequest request,
            @Parameter(hidden = true) HttpServletResponse response) {
        String token = authService.signIn(request);
        response.addCookie(new Cookie("Authorization", token));
        return token;
    }

    @PostMapping("/api/v1/users:signUp")
    public String signUp(
            @Valid @RequestBody SignUpRequest request,
            @Parameter(hidden = true) HttpServletResponse response) {
        authService.signUp(request);
        SignInRequest sign = new SignInRequest();
        sign.setNickname(request.getNickname());
        sign.setPassword(request.getPassword());
        return signIn(sign, response);
    }

    @GetMapping("/api/v1/users/principal")
    public User principal(@Parameter(hidden = true) @AuthenticationPrincipal User principal) {
        principal.setPassword("******");
        return principal;
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PatchMapping("/api/v1/users/{userId}")
    public void patchUser(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @Valid @RequestBody PatchUserRequest request) {
        userService.patchUser(userId, request);
    }
}
