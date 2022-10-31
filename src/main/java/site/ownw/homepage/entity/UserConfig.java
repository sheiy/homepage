package site.ownw.homepage.entity;

import static site.ownw.homepage.common.Instance.STRING_OBJECT_TYPE_REFERENCE;

import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import site.ownw.homepage.common.util.JSONUtil;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserConfig extends AbstractEntity {

    private String key;
    private String value;

    private Long userId;

    public Map<String, Object> getMapValue() {
        return JSONUtil.fromJSON(value, STRING_OBJECT_TYPE_REFERENCE);
    }

    public void setMapValue(Map<String, Object> value) {
        this.value = JSONUtil.toJSON(value);
    }
}
