package notesapi.application.dto.response;

import java.util.List;

public record PaginatedResponse(
        List<NoteResponse> items,
        int currentPage,
        int pageSize,
        long totalItems,
        int totalPages
) {}