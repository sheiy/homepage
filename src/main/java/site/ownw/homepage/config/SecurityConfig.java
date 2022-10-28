package site.ownw.homepage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.ownw.homepage.domain.user.AuthService;

@Slf4j
@Configuration
@EnableWebSecurity()
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    public static final Set<String> IGNORE_LOGIN_ANT_PATTERNS =
            Set.of(
                    "/api/v1/users:signIn",
                    "/api/v1/users:signUp",
                    "/api/v1/configs/**",
                    "/error",
                    "/swagger-ui/**",
                    "/v3/api-docs/**");

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            ObjectMapper objectMapper,
            AuthService authService,
            AuthenticationManager authenticationManager)
            throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(
                        new AuthenticationEntryPoint() {
                            @Override
                            public void commence(
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    AuthenticationException authException)
                                    throws IOException {
                                response.setContentType("application/json;charset=UTF-8");
                                response.setStatus(HttpStatus.FORBIDDEN.value());
                                response
                                        .getWriter()
                                        .write(
                                                objectMapper.writeValueAsString(
                                                        Map.of(
                                                                "path",
                                                                request.getContextPath(),
                                                                "errorCode",
                                                                HttpStatus.FORBIDDEN.value(),
                                                                "errorMsg",
                                                                authException.getLocalizedMessage())));
                                response.getWriter().flush();
                            }
                        })
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(IGNORE_LOGIN_ANT_PATTERNS.toArray(new String[] {}))
                .permitAll()
                .anyRequest()
                .authenticated();
        http.addFilterBefore(
                        new BearAuthenticationFilter(authenticationManager, authService),
                        UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(authService);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
            }
        };
    }
}
