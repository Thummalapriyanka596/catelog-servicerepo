package com.example.catalog.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex)
    {
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error has been occured,Please try again");
    }

    //Adding a exception handler method to handle invalid input
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleErrorException(MethodArgumentNotValidException ex)
    {
        String errorMsg=ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error->error.getField()+":"+error.getDefaultMessage())
                .collect(Collectors.joining(";"));
              //  .orElse("Invalid Input");
        return new ResponseEntity<>(errorMsg,HttpStatus.BAD_REQUEST);
    }

    //Adding a exception handler method to handle empty values
    //   // Handles when JSON is empty or badly formatted
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public  ResponseEntity<String> handleEmptyORBadInput(HttpMessageNotReadableException ex)
    {
        return ResponseEntity.badRequest().body("Request body is missing OR null input");
    }

    //Adding a exception handler method for product not found
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex)
    {
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
    }


}
