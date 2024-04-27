package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = AlreadyExistsException.class)
    protected ResponseEntity<String> handleAlreadyExistsException(AlreadyExistsException exception, WebRequest request) {
        return new ResponseEntity<>("Duplicate", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException exception) {
        return new ResponseEntity<>("Request validation exception", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HeaderNotExistsException.class)
    protected ResponseEntity<String> handleHeaderNotExistsException(HeaderNotExistsException exception) {
        return new ResponseEntity<>("Header not exists", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = UnknownStateException.class)
    protected ResponseEntity<ErrorResponse> handleHeaderUnknownStateException(UnknownStateException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = NotFoundException.class)
    protected ResponseEntity<String> handleNotFoundException(NotFoundException exception, WebRequest request) {
        return new ResponseEntity<>("Entity not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = IllegalCallerException.class)
    protected ResponseEntity<String> handleIllegalCallerException(IllegalCallerException exception) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = ValidationException.class)
    protected ResponseEntity<String> handleValidationException(ValidationException validationException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
