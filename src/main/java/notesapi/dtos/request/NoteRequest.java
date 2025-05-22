package notesapi.dtos.request;

import jakarta.validation.constraints.NotEmpty;

public class NoteRequest {

    @NotEmpty(message = "The title cannot be empty")
    private final String title;

    @NotEmpty(message = "The content cannot be empty")
    private final String content;

    public NoteRequest(String title, String content) {
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
