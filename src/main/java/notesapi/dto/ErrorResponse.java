package notesapi.dto;

public record ErrorResponse(
        String error,
        String message
) {}
