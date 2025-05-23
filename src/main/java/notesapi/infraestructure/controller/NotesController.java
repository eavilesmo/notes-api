package notesapi.infraestructure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import notesapi.application.dto.request.NoteRequest;
import notesapi.application.dto.response.NoteResponse;
import notesapi.application.dto.response.PaginatedResponse;
import notesapi.application.service.NoteService;
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
import reactor.core.publisher.Mono;

import java.util.List;

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
    public Mono<ResponseEntity<NoteResponse>> getNoteById(@PathVariable String id) {
        return noteService.findById(id)
                .map(NoteResponse::from)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    @Operation(summary = "Get all notes")
    public Mono<ResponseEntity<PaginatedResponse>> getAllNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return noteService.findAll(page, size)
                .map(NoteResponse::from)
                .collectList()
                .zipWith(noteService.count())
                .map(tuple -> {
                    List<NoteResponse> notes = tuple.getT1();
                    long totalItems = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalItems / size);
                    return ResponseEntity.ok(new PaginatedResponse(notes, page, size, totalItems, totalPages));
                });
    }

    @GetMapping("/search")
    @Operation(summary = "Search note by keyword")
    public Mono<ResponseEntity<PaginatedResponse>> searchNotes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return noteService.search(keyword, page, size)
                .map(NoteResponse::from)
                .collectList()
                .zipWith(noteService.countByKeyword(keyword))
                .map(tuple -> {
                    List<NoteResponse> notes = tuple.getT1();
                    long totalItems = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalItems / size);
                    return ResponseEntity.ok(new PaginatedResponse(notes, page, size, totalItems, totalPages));
                });
    }

    @PostMapping
    @Operation(summary = "Create a note")
    public Mono<ResponseEntity<NoteResponse>> createNote(@Valid @RequestBody NoteRequest request) {
        return noteService.create(request.toNote())
                .map(NoteResponse::from)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update note")
    public Mono<ResponseEntity<NoteResponse>> updateNote(
            @Valid @RequestBody NoteRequest request,
            @PathVariable String id
    ) {
        return noteService.update(request.toNote(), id)
                .map(NoteResponse::from)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note by ID")
    public Mono<ResponseEntity<Void>> deleteNoteById(@PathVariable String id) {
        return noteService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
