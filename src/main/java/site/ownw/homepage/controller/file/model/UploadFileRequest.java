package site.ownw.homepage.controller.file.model;

import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadFileRequest {

    @Parameter(required = true)
    @NotNull
    private MultipartFile file;

    private Long folderId;
}
