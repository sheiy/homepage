package site.ownw.homepage.domain.user;

import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import site.ownw.homepage.common.exception.BusinessException;
import site.ownw.homepage.controller.user.model.SaveUserConfigRequest;
import site.ownw.homepage.controller.user.model.UpdateUserRequest;
import site.ownw.homepage.domain.user.repository.UserConfigRepository;
import site.ownw.homepage.domain.user.repository.UserRepository;
import site.ownw.homepage.entity.User;
import site.ownw.homepage.entity.UserConfig;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserConfigRepository userConfigRepository;

    public void updateUser(Long userId, @Valid UpdateUserRequest request) {
        User user =
                userRepository.findById(userId).orElseThrow(() -> new BusinessException("User NotFound"));
        user.setDefaultSearchEngine(request.getDefaultSearchEngine());
        userRepository.save(user);
    }

    public Map<String, Object> getUserConfig(Long userId, String configKey) {
        Optional<UserConfig> optionalUserConfig =
                userConfigRepository.findByUserIdAndKey(userId, configKey);
        if (optionalUserConfig.isPresent()) {
            return optionalUserConfig.get().getMapValue();
        } else {
            return Map.of();
        }
    }

    public void saveUserConfig(Long userId, String configKey, SaveUserConfigRequest request) {
        Optional<UserConfig> optionalUserConfig =
                userConfigRepository.findByUserIdAndKey(userId, configKey);
        UserConfig userConfig;
        if (optionalUserConfig.isPresent()) {
            userConfig = optionalUserConfig.get();
            userConfig.setMapValue(request.getValue());
        } else {
            userConfig = new UserConfig();
            userConfig.setKey(configKey);
            userConfig.setMapValue(request.getValue());
            userConfig.setUserId(userId);
        }
        userConfigRepository.save(userConfig);
    }
}
