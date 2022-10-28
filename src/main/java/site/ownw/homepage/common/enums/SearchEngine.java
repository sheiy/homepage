package site.ownw.homepage.common.enums;

import lombok.Getter;

public enum SearchEngine {
    GOOGLE("谷歌"),
    BAI_DU("百度"),
    BING("必应"),
    ;

    @Getter private final String desc;

    SearchEngine(String desc) {
        this.desc = desc;
    }
}
