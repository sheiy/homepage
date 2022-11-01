package site.ownw.homepage.config;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;
import site.ownw.homepage.domain.user.AuthService;
import site.ownw.homepage.entity.Token;
import site.ownw.homepage.entity.User;

@Slf4j
public class BearAuthenticationFilter extends BasicAuthenticationFilter {

    private final AuthService authService;

    public BearAuthenticationFilter(
            AuthenticationManager authenticationManager, AuthService authService) {
        super(authenticationManager);
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String bearer = request.getHeader("Authorization");
        if (!StringUtils.hasText(bearer)) {
            bearer = request.getParameter("authorization");
        }
        if (!StringUtils.hasText(bearer)) {
            chain.doFilter(request, response);
            return;
        }
        if (bearer.startsWith("Bearer")) {
            bearer = bearer.substring(7);
        }
        Optional<Token> token = authService.loadTokenByBearer(bearer);
        if (token.isPresent()) {
            Optional<User> optionalUser = authService.loadUserById(token.get().getUserId());
            if (optionalUser.isEmpty() || !StringUtils.hasText(optionalUser.get().getUsername())) {
                log.error(
                        "can not get username from token, token：{}, URI：{}", bearer, request.getServletPath());
                chain.doFilter(request, response);
                return;
            }
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            optionalUser.get(), null, optionalUser.get().getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);
            return;
        }
        chain.doFilter(request, response);
    }
}
