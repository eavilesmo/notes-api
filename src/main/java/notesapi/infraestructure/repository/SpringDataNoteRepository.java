package notesapi.infraestructure.repository;

import notesapi.domain.model.Note;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SpringDataNoteRepository extends ReactiveMongoRepository<Note, String> {

    @Query("{ $or: [ " +
            "{ 'title': { $regex: ?0, $options: 'i' }}, " +
            "{ 'content': { $regex: ?0, $options: 'i' }}, " +
            "{ 'tags': { $regex: ?0, $options: 'i' }} " +
            "]}")
    Flux<Note> searchByKeyword(String keyword);
}
