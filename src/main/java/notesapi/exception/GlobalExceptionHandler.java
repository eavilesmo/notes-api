package notesapi.exception;

import notesapi.dto.response.ErrorResponse;
import notesapi.dto.response.FieldValidationError;
import notesapi.dto.response.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoteNotFound(NoteNotFoundException ex) {
        ErrorResponse response = new ErrorResponse("Note not found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleInvalidArgument(MethodArgumentNotValidException ex) {
        List<FieldValidationError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        ValidationErrorResponse response = new ValidationErrorResponse(
                "Validation failed",
                "Some fields are missing or invalid",
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequestBody(HttpMessageNotReadableException ex) {
        String error = "Invalid JSON format";
        String message = "The request body is missing or malformed.";
        ErrorResponse response = new ErrorResponse(error, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
