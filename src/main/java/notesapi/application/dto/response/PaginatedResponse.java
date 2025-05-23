package notesapi.application.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
public class PaginatedResponse {
    private List<NoteResponse> items;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;

    public PaginatedResponse(Page<NoteResponse> pageData) {
        this.items = pageData.getContent();
        this.page = pageData.getNumber();
        this.size = pageData.getSize();
        this.totalPages = pageData.getTotalPages();
        this.totalElements = pageData.getTotalElements();
    }
}