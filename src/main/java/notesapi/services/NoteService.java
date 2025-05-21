package notesapi.services;

import notesapi.entities.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteService {

    public Note findById(String id) {
        return new Note();
    }
}
