package notesapi.dtos.response;

import org.springframework.data.domain.Page;

import java.util.List;

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

    public PaginatedResponse() {}

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getSize() {
        return size;
    }

    public int getPage() {
        return page;
    }

    public List<NoteResponse> getItems() {
        return items;
    }
}