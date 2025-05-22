package notesapi.services;

import lombok.AllArgsConstructor;
import notesapi.dtos.request.NoteRequest;
import notesapi.entities.Note;
import notesapi.exceptions.NoteNotFoundException;
import notesapi.repositories.NoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NoteService {

    private NoteRepository noteRepository;

    public Note findById(String id) {
        return noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }

    public Page<Note> findAll(Pageable pageable) {
        return noteRepository.findAll(pageable);
    }

    public Page<Note> search(Pageable pageable, String keyword) {
        return noteRepository.search(pageable, keyword);
    }

    public Note create(NoteRequest request) {
        Note note = new Note();
        note.setTitle(request.title());
        note.setContent(request.content());
        note.setTags(request.tags());
        return noteRepository.save(note);
    }

    public Note update(NoteRequest request, String id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        note.setTitle(request.title());
        note.setContent(request.content());
        note.setTags(request.tags());
        note.refreshUpdatedAt();
        return noteRepository.save(note);
    }

    public void deleteById(String id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        noteRepository.deleteById(note.getId());
    }
}
