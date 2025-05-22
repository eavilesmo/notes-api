package notesapi.dto;

import jakarta.validation.constraints.NotNull;

public class NoteCreateRequest {

    @NotNull(message = "The title cannot be empty")
    private final String title;

    @NotNull(message = "The content cannot be empty")
    private final String content;

    public NoteCreateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
