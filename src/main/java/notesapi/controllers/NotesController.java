package notesapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import notesapi.dtos.request.NoteRequest;
import notesapi.dtos.response.NoteResponse;
import notesapi.entities.Note;
import notesapi.services.NoteService;
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
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable String id) {
        NoteResponse response = NoteResponse.from(noteService.findById(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all notes")
    public ResponseEntity<List<NoteResponse>> getAllNotes() {
        List<NoteResponse> response = noteService.findAll().stream().map(NoteResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search note by keyword")
    public ResponseEntity<List<NoteResponse>> searchNotes(@RequestParam String keyword) {
        List<NoteResponse> response = noteService.search(keyword).stream().map(NoteResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a note")
    public ResponseEntity<NoteResponse> createNote(@Valid @RequestBody NoteRequest request) {
        NoteResponse response = NoteResponse.from(noteService.create(request));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update note")
    public ResponseEntity<NoteResponse> updateNote(@Valid @RequestBody NoteRequest request, @PathVariable String id) {
        NoteResponse response = NoteResponse.from(noteService.update(request, id));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note by ID")
    public ResponseEntity<Void> deleteNoteById(@PathVariable String id) {
        noteService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
