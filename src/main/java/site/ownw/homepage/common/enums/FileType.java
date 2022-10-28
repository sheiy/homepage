package site.ownw.homepage.common.enums;

import lombok.Getter;

public enum FileType {
    FILE("文件"),
    FOLDER("文件夹");

    @Getter private final String desc;

    FileType(String desc) {
        this.desc = desc;
    }
}
