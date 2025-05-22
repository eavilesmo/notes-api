package notesapi.dtos.response;

public record ErrorResponse(
        String error,
        String message
) {}
