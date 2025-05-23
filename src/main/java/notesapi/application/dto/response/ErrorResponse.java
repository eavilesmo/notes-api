package notesapi.application.dto.response;

public record ErrorResponse(
        String error,
        String message
) {}
