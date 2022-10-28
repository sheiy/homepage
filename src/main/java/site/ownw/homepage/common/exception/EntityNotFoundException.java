package site.ownw.homepage.common.exception;

import lombok.Getter;

public class EntityNotFoundException extends RuntimeException {

    @Getter private String entity;
    @Getter private Long id;

    @Getter private String key;

    public EntityNotFoundException(String entity, Long id) {
        super(String.format("%s id:%d not found", entity, id));
        this.id = id;
        this.entity = entity;
    }

    public EntityNotFoundException(String entity, String key) {
        super(String.format("%s key:%s not found", entity, key));
        this.key = key;
        this.entity = entity;
    }
}
