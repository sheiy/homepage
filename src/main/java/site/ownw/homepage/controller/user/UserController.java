package site.ownw.homepage.controller.user;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.ownw.homepage.common.exception.EntityNotFoundException;
import site.ownw.homepage.controller.user.model.CreateUserConfigRequest;
import site.ownw.homepage.controller.user.model.SignInRequest;
import site.ownw.homepage.controller.user.model.SignUpRequest;
import site.ownw.homepage.controller.user.model.UpdateUserConfigRequest;
import site.ownw.homepage.controller.user.model.UpdateUserRequest;
import site.ownw.homepage.domain.user.AuthService;
import site.ownw.homepage.domain.user.UserService;
import site.ownw.homepage.domain.user.repository.UserConfigRepository;
import site.ownw.homepage.entity.User;
import site.ownw.homepage.entity.UserConfig;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final UserConfigRepository userConfigRepository;

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
    @PutMapping("/api/v1/users/{userId}")
    public void updateUser(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        userService.updateUser(userId, request);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @GetMapping("/api/v1/users/{userId}/configs/{configKey}")
    public Map<String, Object> getUserConfig(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable String configKey) {
        return userService.getUserConfig(userId, configKey);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PutMapping("/api/v1/users/{userId}/configs/{configKey}")
    public void updateUserConfig(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable String configKey,
            @RequestBody @Valid UpdateUserConfigRequest request) {
        UserConfig userConfig =
                userConfigRepository
                        .findByUserIdAndKey(userId, configKey)
                        .orElseThrow(() -> new EntityNotFoundException("UserConfig", configKey));
        if (!userConfig.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        userService.updateUserConfig(userId, configKey, request);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PostMapping("/api/v1/users/{userId}/configs/{configKey}")
    public void createUserConfig(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable String configKey,
            @RequestBody @Valid CreateUserConfigRequest request) {
        userService.createUserConfig(userId, configKey, request);
    }
}
