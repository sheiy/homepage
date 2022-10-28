package site.ownw.homepage.common;

import java.time.ZoneId;

public abstract class Instance {

    /** ！！注意这里一定要和配置文件里JDBC URL的时区设置保持一直，否则时间会发生错乱！！ */
    public static final ZoneId ZONE_ID = ZoneId.of("GMT+8");
}
