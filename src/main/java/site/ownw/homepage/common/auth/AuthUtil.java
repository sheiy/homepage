package site.ownw.homepage.common.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import site.ownw.homepage.entity.User;

@Component
public class AuthUtil {

    public boolean isMe(Long userId) {
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null
                || SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null
                || !(SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                        instanceof User)) {
            return false;
        }
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getId().equals(userId);
    }
}
