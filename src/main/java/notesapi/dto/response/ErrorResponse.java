package notesapi.dto.response;

public record ErrorResponse(
        String error,
        String message
) {}
