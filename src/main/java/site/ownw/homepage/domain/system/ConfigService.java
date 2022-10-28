package site.ownw.homepage.domain.system;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.ownw.homepage.common.exception.EntityNotFoundException;
import site.ownw.homepage.domain.system.repository.SystemConfigRepository;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final SystemConfigRepository systemConfigRepository;

    public Map<String, Object> getConfig(String key) {
        return systemConfigRepository
                .findByKey(key)
                .orElseThrow(() -> new EntityNotFoundException("SystemConfig", key))
                .getMapContent();
    }
}
