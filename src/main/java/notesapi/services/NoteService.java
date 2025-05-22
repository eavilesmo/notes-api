package notesapi.services;

import notesapi.dtos.request.NoteRequest;
import notesapi.entities.Note;
import notesapi.exceptions.NoteNotFoundException;
import notesapi.repositories.NoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Note> findAll(Pageable pageable) {
        return noteRepository.findAll(pageable);
    }

    public List<Note> search(String keyword) {
        return noteRepository.search(keyword);
    }

    public Note create(NoteRequest request) {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setTags(request.getTags());
        return noteRepository.save(note);
    }

    public Note update(NoteRequest request, String id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setTags(request.getTags());
        note.refreshUpdatedAt();
        return noteRepository.save(note);
    }

    public void deleteById(String id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        noteRepository.deleteById(note.getId());
    }
}
