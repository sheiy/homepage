package site.ownw.homepage.controller.auth.model;

import lombok.Data;
import site.ownw.homepage.common.enums.SearchEngine;

@Data
public class PatchUserRequest {

    private SearchEngine defaultSearchEngine;
}
