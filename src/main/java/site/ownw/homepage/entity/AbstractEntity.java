package site.ownw.homepage.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Data
public abstract class AbstractEntity {

    @Schema(required = true, implementation = String.class)
    @Id
    private Long id;

    @Schema(required = true)
    @CreatedDate
    private OffsetDateTime createdAt;

    @Schema(required = true)
    @LastModifiedDate
    private OffsetDateTime updatedAt;

    @Schema(required = true, implementation = String.class)
    @CreatedBy
    private Long createdBy;

    @Schema(required = true, implementation = String.class)
    @LastModifiedBy
    private Long updatedBy;
}
