package site.ownw.homepage.domain.system;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.ownw.homepage.domain.system.repository.SystemConfigRepository;
import site.ownw.homepage.entity.SystemConfig;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final SystemConfigRepository systemConfigRepository;

    public Map<String, Object> getConfig(String key) {
        Optional<SystemConfig> systemConfig = systemConfigRepository.findByKey(key);
        if (systemConfig.isPresent()) {
            return systemConfig.get().getMapContent();
        } else {
            return Map.of();
        }
    }
}
