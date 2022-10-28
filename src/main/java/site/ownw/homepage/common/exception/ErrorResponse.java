package site.ownw.homepage.common.exception;

import lombok.Data;

@Data
public class ErrorResponse {

    private String errorCode;

    private String errorMsg;

    private String errorDetail;

    public ErrorResponse(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ErrorResponse(String errorCode, String errorMsg, String errorDetail) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.errorDetail = errorDetail;
    }
}
