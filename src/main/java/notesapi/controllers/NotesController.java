package notesapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import notesapi.dtos.request.NoteRequest;
import notesapi.entities.Note;
import notesapi.services.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<Note> getNoteById(@PathVariable String id) {
        Note note = noteService.findById(id);
        return ResponseEntity.ok(note);
    }

    @GetMapping
    @Operation(summary = "Get all notes")
    public ResponseEntity<List<Note>> getAllNotes() {
        List<Note> notes = noteService.findAll();
        return ResponseEntity.ok(notes);
    }

    @PostMapping
    @Operation(summary = "Create a note")
    public ResponseEntity<Note> createNote(@Valid @RequestBody NoteRequest request) {
        Note note = noteService.create(request);
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note by ID")
    public ResponseEntity<Void> deleteNoteById(@PathVariable String id) {
        noteService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
