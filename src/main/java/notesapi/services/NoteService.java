package notesapi.services;

import notesapi.entities.Note;
import notesapi.exception.NoteNotFoundException;
import notesapi.repositories.NoteRepository;
import org.springframework.stereotype.Component;

@Component
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note findById(String id) {
        return noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }
}
