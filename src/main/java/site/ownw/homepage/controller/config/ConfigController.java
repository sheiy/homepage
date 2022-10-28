package site.ownw.homepage.controller.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import site.ownw.homepage.domain.system.ConfigService;

@RestController
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @GetMapping("/api/v1/configs/{key}")
    public Map<String, Object> getConfig(@PathVariable String key) {
        return configService.getConfig(key);
    }
}
