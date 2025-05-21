package notesapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotesController {

    @GetMapping("/notes")
    public ResponseEntity<String> getNoteById() {
        return ResponseEntity.ok("Here's a note");
    }
}
