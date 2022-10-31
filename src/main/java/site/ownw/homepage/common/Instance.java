package site.ownw.homepage.common;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.ZoneId;
import java.util.Map;

public abstract class Instance {

    /** ！！注意这里一定要和配置文件里JDBC URL的时区设置保持一直，否则时间会发生错乱！！ */
    public static final ZoneId ZONE_ID = ZoneId.of("GMT+8");

    public static final TypeReference<Map<String, Object>> STRING_OBJECT_TYPE_REFERENCE =
            new TypeReference<>() {};
}
