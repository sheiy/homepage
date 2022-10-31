package site.ownw.homepage.domain.user;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import site.ownw.homepage.common.Instance;
import site.ownw.homepage.common.exception.BusinessException;
import site.ownw.homepage.controller.user.model.SignInRequest;
import site.ownw.homepage.controller.user.model.SignUpRequest;
import site.ownw.homepage.domain.user.repository.TokenRepository;
import site.ownw.homepage.domain.user.repository.UserRepository;
import site.ownw.homepage.entity.Token;
import site.ownw.homepage.entity.User;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private static final Long TOKEN_VALID_SECONDS = 7 * 24 * 60 * 60L;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final HttpServletRequest servletRequest;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest httpServletRequest;

    public Optional<Token> loadTokenByBearer(String Bearer) {
        Optional<Token> optionalToken = tokenRepository.findFirstByBearer(Bearer);
        if (optionalToken.isEmpty()) {
            return optionalToken;
        }
        Token token = optionalToken.get();
        if (!token.getValid()
                || OffsetDateTime.now(token.getGeneratedAt().getOffset())
                        .isAfter(token.getGeneratedAt().plusSeconds(TOKEN_VALID_SECONDS))) {
            return Optional.empty();
        }
        return Optional.of(token);
    }

    @Override
    public User loadUserByUsername(String nickname) throws UsernameNotFoundException {
        return userRepository
                .findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException(""));
    }

    public Optional<User> loadUserById(Long userId) throws UsernameNotFoundException {
        return userRepository.findById((userId));
    }

    public String signIn(@Valid SignInRequest request) {
        User user = this.loadUserByUsername(request.getNickname());
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            Token token = new Token();
            token.setBearer(UUID.randomUUID().toString());
            token.setValid(true);
            token.setGeneratedForIp(servletRequest.getRemoteAddr());
            token.setUserId(user.getId());
            token.setGeneratedAt(OffsetDateTime.now(Instance.ZONE_ID));
            tokenRepository.save(token);
            return token.getBearer();
        }
        throw new UsernameNotFoundException("");
    }

    public void signUp(@Valid SignUpRequest request) {
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new BusinessException("Username already exist");
        }
        User user = new User();
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }
}
