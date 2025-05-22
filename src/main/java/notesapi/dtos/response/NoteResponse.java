package notesapi.dtos.response;

import notesapi.entities.Note;

import java.time.LocalDateTime;
import java.util.List;

public record NoteResponse(
        String id,
        String title,
        String content,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NoteResponse from(Note note) {
        return new NoteResponse(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getTags(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}
