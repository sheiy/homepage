package site.ownw.homepage.common.exception;

import lombok.Getter;

public class BusinessException extends RuntimeException {

    @Getter private String detail;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, String detail) {
        super(message);
        this.detail = detail;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message, String detail, Throwable cause) {
        super(message, cause);
        this.detail = detail;
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }
}
