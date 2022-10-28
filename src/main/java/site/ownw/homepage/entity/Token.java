package site.ownw.homepage.entity;

import java.time.OffsetDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Token extends AbstractEntity {

    private String bearer;

    private Long userId;

    private OffsetDateTime generatedAt;

    private String generatedForIp;

    private Boolean valid;
}
