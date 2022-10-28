package site.ownw.homepage.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.ownw.homepage.common.util.JSONUtil;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SystemConfig extends AbstractEntity {

    private static final TypeReference<Map<String, Object>> STRING_OBJECT_TYPE_REFERENCE =
            new TypeReference<>() {};

    @Getter @Setter private String key;

    @Getter @Setter private String content;

    public Map<String, Object> getMapContent() {
        return JSONUtil.fromJSON(content, STRING_OBJECT_TYPE_REFERENCE);
    }

    public void setMapContent(Map<String, Object> content) {
        this.content = JSONUtil.toJSON(content);
    }
}
