package site.ownw.homepage.domain.user;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import site.ownw.homepage.common.exception.BusinessException;
import site.ownw.homepage.controller.auth.model.PatchUserRequest;
import site.ownw.homepage.domain.user.repository.UserRepository;
import site.ownw.homepage.entity.User;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void patchUser(Long userId, @Valid PatchUserRequest request) {
        User user =
                userRepository.findById(userId).orElseThrow(() -> new BusinessException("User NotFound"));
        user.setDefaultSearchEngine(request.getDefaultSearchEngine());
        userRepository.save(user);
    }
}
