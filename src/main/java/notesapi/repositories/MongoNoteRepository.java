package notesapi.repositories;

import notesapi.entities.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoNoteRepository extends NoteRepository, MongoRepository<Note, String> {

    @Query("{ $or: [ " +
            "{ 'title': { $regex: ?0, $options: 'i' }}, " +
            "{ 'content': { $regex: ?0, $options: 'i' }}, " +
            "{ 'tags': { $regex: ?0, $options: 'i' }} " +
            "]}")
    List<Note> search(String keyword);
}
