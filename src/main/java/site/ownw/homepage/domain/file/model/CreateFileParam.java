package site.ownw.homepage.domain.file.model;

import java.io.InputStream;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFileParam {

    @NotNull private InputStream inputStream;

    @NotEmpty private String contentType;

    @NotEmpty private String fileName;

    @NotNull private Long size;

    private Long folderId;
}
