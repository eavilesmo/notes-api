package notesapi.infraestructure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import notesapi.application.dto.request.NoteRequest;
import notesapi.application.dto.response.NoteResponse;
import notesapi.application.dto.response.PaginatedResponse;
import notesapi.application.service.NoteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes")
@Tag(name = "Notes", description = "API for managing notes")
public class NotesController {

    private final NoteService noteService;

    public NotesController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get note by ID")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable String id) {
        NoteResponse response = NoteResponse.from(noteService.findById(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all notes")
    public ResponseEntity<PaginatedResponse> getAllNotes(@PageableDefault(
            page = 0,
            size = 10,
            sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<NoteResponse> page = noteService.findAll(pageable).map(NoteResponse::from);
        return ResponseEntity.ok(new PaginatedResponse(page));
    }

    @GetMapping("/search")
    @Operation(summary = "Search note by keyword")
    public ResponseEntity<PaginatedResponse> searchNotes(@RequestParam String keyword, @PageableDefault(
            page = 0,
            size = 10,
            sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {
        Page<NoteResponse> page = noteService.search(pageable, keyword).map(NoteResponse::from);
        return ResponseEntity.ok(new PaginatedResponse(page));
    }

    @PostMapping
    @Operation(summary = "Create a note")
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest request) {
        NoteResponse response = NoteResponse.from(noteService.create(request.toNote()));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update note")
    public ResponseEntity<NoteResponse> updateNote(@Valid @RequestBody NoteRequest request, @PathVariable String id) {
        NoteResponse response = NoteResponse.from(noteService.update(request.toNote(), id));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note by ID")
    public ResponseEntity<Void> deleteNoteById(@PathVariable String id) {
        noteService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
