package notesapi.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import notesapi.domain.model.Note;

import java.util.List;

public record NoteRequest(@NotEmpty(message = "The title cannot be empty") String title,
                          @NotEmpty(message = "The content cannot be empty") String content,
                          @NotEmpty(message = "The tags cannot be empty") List<String> tags) {

    public Note toNote() {
        return Note.builder()
                .title(this.title)
                .content(this.content)
                .tags(this.tags)
                .build();
    }
}
