package notesapi.repositories;

import notesapi.entities.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoNoteRepository extends NoteRepository, MongoRepository<Note, String> {
}
