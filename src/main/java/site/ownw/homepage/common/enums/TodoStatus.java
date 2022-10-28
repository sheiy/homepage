package site.ownw.homepage.common.enums;

import lombok.Getter;

public enum TodoStatus {
    CREATED("已创建"),
    COMPLETED("已完成"),
    ;

    @Getter private final String desc;

    TodoStatus(String desc) {
        this.desc = desc;
    }
}
