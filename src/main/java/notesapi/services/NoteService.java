package notesapi.services;

import notesapi.dto.request.NoteCreateRequest;
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

    public Note create(NoteCreateRequest request) {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        return noteRepository.save(note);
    }

    public void deleteById(String id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        noteRepository.deleteById(note.getId());
    }
}
