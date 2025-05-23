package notesapi.domain.repository;

import notesapi.domain.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NoteRepository {

    Optional<Note> findById(String id);
    Note save(Note note);
    void deleteAll();
    void deleteById(String id);
    Page<Note> findAll(Pageable pageable);
    Page<Note> search(Pageable pageable, String keyword);
}
