package site.ownw.homepage.common.enums;

import lombok.Getter;

public enum TodoItemRepeatStrategy {
    NONE("不重复"),
    ;

    @Getter private final String desc;

    TodoItemRepeatStrategy(String desc) {
        this.desc = desc;
    }
}
