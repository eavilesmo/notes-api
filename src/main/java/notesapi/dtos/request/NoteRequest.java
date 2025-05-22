package notesapi.dtos.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class NoteRequest {

    @NotEmpty(message = "The title cannot be empty")
    private final String title;

    @NotEmpty(message = "The content cannot be empty")
    private final String content;

    @NotEmpty(message = "The tags cannot be empty")
    private final List<String> tags;

    public NoteRequest(String title, String content, List<String> tags) {
        this.title = title;
        this.content = content;
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<String> getTags() {
        return tags;
    }

}
