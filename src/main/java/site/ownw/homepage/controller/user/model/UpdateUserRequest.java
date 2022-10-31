package site.ownw.homepage.controller.user.model;

import lombok.Data;
import site.ownw.homepage.common.enums.SearchEngine;

@Data
public class UpdateUserRequest {

    private SearchEngine defaultSearchEngine;
}
