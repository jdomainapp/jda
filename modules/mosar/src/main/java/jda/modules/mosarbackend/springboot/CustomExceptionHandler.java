package jda.modules.mosarbackend.springboot;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jda.modules.common.exceptions.ApplicationException;
import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.QueryException;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private final ObjectMapper objectMapper;

    @Autowired
    public CustomExceptionHandler(MappingJackson2HttpMessageConverter springMvcJacksonConverter) {
        objectMapper = springMvcJacksonConverter.getObjectMapper();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final ApiError apiError = new ApiError(ex.getLocalizedMessage(), errors);
        try {
            final String json = objectMapper.writeValueAsString(apiError);
            return handleExceptionInternal(ex, json, headers, status, request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        String errText = ex.getCode().getText();
        String message = ex.getMessage();
        final ApiError apiError = new ApiError(message, errText);
        try {
            final String json = objectMapper.writeValueAsString(apiError);
            return handleExceptionInternal(
                    ex, json, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @ExceptionHandler({ NotFoundException.class, NoSuchElementException.class })
    public ResponseEntity<Object> handleNotFoundException(
            RuntimeException ex, WebRequest request) {
        String errText = ex instanceof NotFoundException ?
                ((NotFoundException)ex).getCode().getText() : ex.getMessage();
        String message = ex.getMessage();
        final ApiError apiError = new ApiError(message, errText);
        return handleExceptionInternal(
                ex, apiError, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ex.printStackTrace();
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<Object> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        ex.printStackTrace();
        Throwable cause = ex;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        cause.printStackTrace();
        if (cause == null) return handleExceptionInternal(
                ex, new Object(), new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
        if (cause instanceof DataSourceException
                || cause instanceof QueryException) {
            return handleDataException((ApplicationException) cause, request);
        }
        if (cause instanceof ConstraintViolationException) {
            return handleConstraintViolation((ConstraintViolationException) cause, request);
        }
        if (cause instanceof NotFoundException || cause instanceof NoSuchElementException) {
            return handleNotFoundException((RuntimeException) cause, request);
        }
        if (cause instanceof NotPossibleException) {
            return handleOtherException((ApplicationRuntimeException) cause, request);
        }

        System.out.println("HANDLING EXCEPTION WITH CAUSE: " + cause);

        return handleExceptionInternal(
                ex, cause, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // TODO: get the deepest exception
    private ResponseEntity<Object> handleOtherException(
            ApplicationRuntimeException ex, WebRequest request) {
        String errText = ex.getCode().getText();
        String message = ex.getMessage();
        final ApiError apiError = new ApiError(message, errText);
        return handleExceptionInternal(
                ex, apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<Object> handleDataException(
            ApplicationException ex, WebRequest request) {
        String errText = ex.getCode().getText();
        String message = ex.getMessage();
        final ApiError apiError = new ApiError(message, errText);

        return handleExceptionInternal(
                ex, apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
