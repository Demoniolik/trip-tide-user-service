package com.travel.trip.tide.user.exception.handler;

import com.travel.trip.tide.user.exception.UserEmailAlreadyOccupied;
import com.travel.trip.tide.user.exception.UserNotFoundException;
import com.travel.trip.tide.user.model.error.ErrorResponseModel;
import com.travel.trip.tide.user.model.error.UserValidationErrorResponseModel;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    private final ModelMapper modelMapper;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseModel> handleUserNotFoundException(
            UserNotFoundException userNotFoundException
    ) {
        var errorResponse = modelMapper
                .map(userNotFoundException, ErrorResponseModel.class);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserEmailAlreadyOccupied.class)
    public ResponseEntity<ErrorResponseModel> handleUserEmailAlreadyInUse(
            UserEmailAlreadyOccupied exception
    ) {
        var errorResponse = modelMapper
                .map(exception, ErrorResponseModel.class);

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        var bindingResult = ex.getBindingResult();
        var fieldErrors = bindingResult.getFieldErrors();
        var errors = new ArrayList<String>();
        fieldErrors.forEach(error ->
                errors.add(error.getField() + ": " + error.getDefaultMessage())
        );
        return new ResponseEntity<>(
                new UserValidationErrorResponseModel(
                        "user.validation.error",
                        errors
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
