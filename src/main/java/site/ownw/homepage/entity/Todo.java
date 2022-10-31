package site.ownw.homepage.entity;

import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import site.ownw.homepage.common.enums.TodoStatus;

@Data
@EqualsAndHashCode(callSuper = true)
public class Todo extends AbstractEntity {

    private String content;

    private TodoStatus status;

    private OffsetDateTime remindDateTime;

    private Long userId;
}
