package jda.modules.restfstool.backend.spring;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ApiError {

    private String message;
    private List<String> errors;

    public ApiError(String message, List<String> errors) {
        super();
        this.message = message;
        this.errors = errors;
    }

    public ApiError(String message, String error) {
        super();
        this.message = message;
        errors = List.of(error);
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }
}