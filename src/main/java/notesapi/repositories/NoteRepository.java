package notesapi.repositories;

import notesapi.entities.Note;

import java.util.Optional;

public interface NoteRepository {

    Optional<Note> findById(String id);
}
