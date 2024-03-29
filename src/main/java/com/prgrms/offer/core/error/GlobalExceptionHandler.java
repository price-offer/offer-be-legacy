package com.prgrms.offer.core.error;

import com.prgrms.offer.common.ApiResponse;
import com.prgrms.offer.common.message.ResponseMessage;
import com.prgrms.offer.core.error.exception.BusinessException;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * body의 validation 실패 시, 예외처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        log.info(exception.getAllErrors().get(0).getDefaultMessage() + " from MethodArgumentNotValidException");
        return createApiExceptionResult(HttpStatus.BAD_REQUEST, exception.getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * path요소의 validation 실패 시, 예외처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolationException(ConstraintViolationException exception){
        log.info(exception.getMessage() + " from ConstraintViolationException");
        return createApiExceptionResult(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * 데이터 바인딩 실패 시, 예외처리
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse> handleBindException(BindException exception){
        log.info(exception.getAllErrors().get(0).getDefaultMessage() + " from BindException");
        return createApiExceptionResult(HttpStatus.BAD_REQUEST, exception.getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> handleBusinessException(BusinessException e) {
        log.info(e.getMessage() + " from BusinessException");
        final ResponseMessage responseMessage = e.getResponseMessage();
        return createApiExceptionResult(responseMessage);
    }

    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.info(exception.getMessage() + " from HttpRequestMethodNotSupportedException");
        return createApiExceptionResult(ResponseMessage.HTTP_REQUEST_METHOD_NOT_SUPPORTED);
    }

    /**
     * request param, body 등에서 argument 타입이 올바르지 않을 때 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.info(exception.getMessage() + " from MethodArgumentTypeMismatchException");
        return createApiExceptionResult(ResponseMessage.INVALID_REQUEST_ARGUMENT_TYPE);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        log.info(exception.getMessage() + " from MissingServletRequestParameterException");
        return createApiExceptionResult(ResponseMessage.MISSING_PARAMETER);
    }

    /**
     * 파일 용량 초과시 발생
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleFileSizeLimitExceededException(MaxUploadSizeExceededException exception) {
        log.info(exception.getMessage() + " from FileSizeLimitExceededException");
        return createApiExceptionResult(ResponseMessage.FILE_SIZE_LIMIT_EXCEEDED);
    }

    private ResponseEntity createApiExceptionResult(HttpStatus httpStatus, String message) {
        return ResponseEntity.ok(ApiResponse.of(httpStatus, message));
    }

    private ResponseEntity createApiExceptionResult(ResponseMessage responseMessage) {
        return ResponseEntity.ok(ApiResponse.of(responseMessage));
    }
}
