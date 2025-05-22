package notesapi.repositories;

import notesapi.entities.Note;

import java.util.List;
import java.util.Optional;

public interface NoteRepository {

    Optional<Note> findById(String id);
    Note save(Note note);
    void deleteAll();
    void deleteById(String id);
    List<Note> findAll();
    List<Note> search(String keyword);
}
