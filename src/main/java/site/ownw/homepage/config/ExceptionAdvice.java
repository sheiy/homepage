package site.ownw.homepage.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.ownw.homepage.common.exception.BusinessException;
import site.ownw.homepage.common.exception.EntityNotFoundException;
import site.ownw.homepage.common.exception.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ErrorResponse handle(MethodArgumentNotValidException exception) {
        return new ErrorResponse("400", "Invalid Argument", exception.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {BusinessException.class})
    public ErrorResponse handle(BusinessException exception) {
        if (exception.getSuppressed().length != 0) {
            log.error("business exception: {}", exception.getLocalizedMessage(), exception);
        }
        return new ErrorResponse("400", exception.getLocalizedMessage(), exception.getDetail());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handle(EntityNotFoundException exception) {
        return new ErrorResponse("404", exception.getLocalizedMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Exception exception) {
        log.error("unexpect error", exception);
        return new ErrorResponse(
                "500", "System Logic Error. Please try again later.", exception.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handle(AccessDeniedException exception) {
        return new ErrorResponse("403", "Forbidden", exception.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse handle(AuthenticationException exception) {
        return new ErrorResponse("401", "Unauthorized", exception.getLocalizedMessage());
    }
}
