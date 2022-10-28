package site.ownw.homepage.domain.file.model;

import java.io.InputStream;
import lombok.Data;

@Data
public class GetFileResult {

    private InputStream inputStream;
    private String name;
    private String contentType;
    private Long size;
}
