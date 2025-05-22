package notesapi.repositories;

import notesapi.entities.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoNoteRepository extends NoteRepository, MongoRepository<Note, String> {

    @Query("{ $or: [ " +
            "{ 'title': { $regex: ?0, $options: 'i' }}, " +
            "{ 'content': { $regex: ?0, $options: 'i' }}, " +
            "{ 'tags': { $regex: ?0, $options: 'i' }} " +
            "]}")
    Page<Note> search(Pageable pageable, String keyword);
}
