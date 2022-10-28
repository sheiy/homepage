package site.ownw.homepage.config;

import static site.ownw.homepage.common.Instance.ZONE_ID;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import site.ownw.homepage.entity.User;

@Configuration
@EnableJdbcAuditing(dateTimeProviderRef = "offsetDateTimeProvider")
public class JdbcAuditingConfig extends AbstractJdbcConfiguration {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            if (SecurityContextHolder.getContext() == null
                    || SecurityContextHolder.getContext().getAuthentication() == null
                    || SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null
                    || !(SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                            instanceof User)) {
                return Optional.of(-1L);
            }
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return Optional.of(principal.getId());
        };
    }

    @Bean("offsetDateTimeProvider")
    public DateTimeProvider offsetDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now(ZONE_ID));
    }

    @Override
    protected List<?> userConverters() {
        return List.of(new LocalDateTimeToOffsetDateTimeConverter());
    }
}
