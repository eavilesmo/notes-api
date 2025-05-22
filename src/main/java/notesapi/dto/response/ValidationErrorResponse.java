package notesapi.dto.response;

import java.util.List;

public record ValidationErrorResponse(
        String error,
        String message,
        List<FieldValidationError> fields
) {}
