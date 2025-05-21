package notesapi.services;

import notesapi.entities.Note;
import notesapi.repositories.NoteRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Optional<Note> findById(String id) {
        return noteRepository.findById(id);
    }
}
