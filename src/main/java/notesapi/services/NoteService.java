package notesapi.services;

import notesapi.entities.Note;
import notesapi.exception.NoteNotFoundException;
import notesapi.repositories.NoteRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note findById(String id) {
        return noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }

    public List<Note> findAll() {
        return noteRepository.findAll();
    }

    public void deleteById(String id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        noteRepository.deleteById(note.getId());
    }
}
