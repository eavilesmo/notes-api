package notesapi.dtos.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record NoteRequest(@NotEmpty(message = "The title cannot be empty") String title,
                          @NotEmpty(message = "The content cannot be empty") String content,
                          @NotEmpty(message = "The tags cannot be empty") List<String> tags) {}
