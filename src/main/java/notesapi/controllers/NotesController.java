package notesapi.controllers;

import notesapi.entities.Note;
import notesapi.services.NoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/notes")
public class NotesController {

    private final NoteService noteService;

    public NotesController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable String id) {
        Optional<Note> note = noteService.findById(id);
        if (note.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(note.get());
    }
}
