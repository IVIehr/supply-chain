package ir.alimojahed.general.elasticwrapper.filter;


import ir.alimojahed.general.elasticwrapper.exception.ExceptionStatus;
import ir.alimojahed.general.elasticwrapper.exception.JsonProcessException;
import ir.alimojahed.general.elasticwrapper.exception.ProjectException;
import ir.alimojahed.general.elasticwrapper.util.ResponseUtil;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;


/**
 * @author Ali Mojahed on 2/14/2021
 * @project Notinou
 **/
@Log4j2
@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(ProjectException.class)
    public ResponseEntity notinouExceptionHandler(WebRequest request, ProjectException exception) {
        log.error(exception.getMessage());
        return handleExceptionInternal(exception,
                ResponseUtil.getErrorGenericResponse(exception),
                new HttpHeaders(),
                HttpStatus.valueOf(exception.getStatus().getCode() != 0 ? exception.getStatus().getCode() : 200),
                request);
    }


    @ExceptionHandler({JSONException.class, JsonProcessException.class})
    public ResponseEntity<Object> jsonExceptionHandler(WebRequest request, Exception exception) {
        log.error(exception.getMessage());
        exception.printStackTrace();
        return handleExceptionInternal(exception,
                ResponseUtil.getErrorGenericResponse(new ProjectException(ExceptionStatus.INVALID_JSON)),
                new HttpHeaders(),
                HttpStatus.valueOf(ExceptionStatus.INVALID_JSON.getCode()),
                request
        );
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> onConstraintValidationException(WebRequest request, ConstraintViolationException exception) {
        log.error(exception.getMessage());

        return handleExceptionInternal(exception,
                ResponseUtil.getErrorGenericResponse(new ProjectException(ExceptionStatus.INVALID_INPUT, exception.getMessage())),
                new HttpHeaders(),
                HttpStatus.valueOf(ExceptionStatus.INVALID_INPUT.getCode()),
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error(ex.getMessage());

        ArrayList<String> allErrors = new ArrayList<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            allErrors.add(fieldError.getField() + " " + fieldError.getDefaultMessage());
        }

        for (ObjectError objectError : ex.getBindingResult().getGlobalErrors()) {
            allErrors.add(objectError.getObjectName() + " " + objectError.getDefaultMessage());
        }

        return handleExceptionInternal(ex,
                ResponseUtil.getErrorGenericResponse(new ProjectException(ExceptionStatus.INVALID_INPUT, allErrors.get(0))),
                new HttpHeaders(),
                HttpStatus.valueOf(ExceptionStatus.INVALID_INPUT.getCode()),
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        log.error(ex);
//        logRequestResponse(body, status);

        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> accessDeniedExceptionHandler(WebRequest request, AccessDeniedException exception) {
        log.error(exception.getMessage());
        return handleExceptionInternal(exception,
                ResponseUtil.getErrorGenericResponse(new ProjectException(ExceptionStatus.ACCESS_DENIED)),
                new HttpHeaders(),
                HttpStatus.UNAUTHORIZED,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> unknownExceptionHandler(WebRequest request, Exception exception) {
        log.error(exception != null ? exception.getMessage() : "unknown error");
        exception.printStackTrace();
        return handleExceptionInternal(exception,
                ResponseUtil.getErrorGenericResponse(new ProjectException(ExceptionStatus.UNKNOWN_ERROR)),
                new HttpHeaders(),
                HttpStatus.valueOf(ExceptionStatus.UNKNOWN_ERROR.getCode()),
                request);
    }

//    @SneakyThrows
//    private void logRequestResponse(Object body, HttpStatus status) {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
//                .getRequest();
//        requestResponseLogger.logError(body, request, status);
//    }

}
