package notesapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes")
public class NotesController {

    @GetMapping("/{id}")
    public ResponseEntity<String> getNoteById(@PathVariable String id) {
        return ResponseEntity.ok("Here's a note with id: " + id);
    }
}
