package notesapi.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "notes")
@Getter
@AllArgsConstructor
public class Note {

    @Id
    private String id;
    private String title;
    private String content;
    private List<String> tags = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Note() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void refreshUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}