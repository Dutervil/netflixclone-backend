package com.wd.netflixcloneback.exception;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("BadCredentialsException {} ",ex.getMessage());
        return getErrorResponse(HttpStatus.UNAUTHORIZED,ex.getMessage());
    }

    @ExceptionHandler(AccountDeactivatedException.class)
    public ResponseEntity<Map<String, Object>> handleAccountDeactivatedException(AccountDeactivatedException ex) {
        log.warn("AccountDeactivatedException  {} ", ex.getMessage());
        return getErrorResponse(HttpStatus.UNAUTHORIZED,ex.getMessage());
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<Map<String, Object>> handleEmailNotVerifiedException(EmailNotVerifiedException ex) {
        log.warn("EmailNotVerifiedException  {} ", ex.getMessage());
        return getErrorResponse(HttpStatus.FORBIDDEN,ex.getMessage());
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<Map<String, Object>> handleEmailSendingException(EmailSendingException ex) {
        log.warn("EmailSendingException  {} ", ex.getMessage());
        return getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        log.warn("InvalidCredentialsException  {} ", ex.getMessage());
        return getErrorResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("ResourceNotFoundException:  {} ", ex.getMessage());
        return getErrorResponse(HttpStatus.NOT_FOUND,ex.getMessage());    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTokenException(InvalidTokenException ex) {
        log.warn("InvalidTokenException:  {} ",  ex.getMessage());
        return getErrorResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRoleException(InvalidRoleException ex) {
        log.warn("InvalidRoleException:   {} ",ex.getMessage());
        return getErrorResponse(HttpStatus.BAD_REQUEST,ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExistException(EmailAlreadyExistException ex) {
        log.warn("EmailAlreadyExistException: {}", ex.getMessage());
        return getErrorResponse(HttpStatus.CONFLICT,ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("MethodArgumentNotValidException:  {} ",ex.getMessage());
         String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return getErrorResponse(HttpStatus.BAD_REQUEST,message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        log.warn("Exception  {} ", ex.getCause(),ex);
        return getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage());
    }


    @ExceptionHandler({AsyncRequestNotUsableException.class, ClientAbortException.class})
    public void  handleClientAbort(ClientAbortException  ex) {
        log.debug("client closed connection during streaming ( expected for video seeking/buffering) :{} ", ex.getMessage());

    }


    private ResponseEntity<Map<String, Object>> getErrorResponse(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("error", message);
        return  ResponseEntity.status(status).body(response);

    }
}
