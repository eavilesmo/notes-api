package notesapi.infraestructure.repository;

import lombok.AllArgsConstructor;
import notesapi.domain.model.Note;
import notesapi.domain.repository.NoteRepository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class MongoNoteRepository implements NoteRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Note> findById(String id) {
        return mongoTemplate.findById(id, Note.class);
    }

    @Override
    public Mono<Note> save(Note note) {
        return mongoTemplate.save(note);
    }

    @Override
    public Mono<Void> deleteAll() {
        return mongoTemplate.remove(new Query(), Note.class).then();
    }

    @Override
    public Mono<Void> deleteById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.remove(query, Note.class).then();
    }

    @Override
    public Mono<Long> count() {
        return mongoTemplate.count(new Query(), Note.class);
    }

    @Override
    public Flux<Note> findAll(int page, int size) {
        Query query = new Query()
                .skip((long) page * size)
                .limit(size)
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));

        return mongoTemplate.find(query, Note.class);
    }

    @Override
    public Flux<Note> search(String keyword, int page, int size) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("title").regex(keyword, "i"),
                Criteria.where("content").regex(keyword, "i"),
                Criteria.where("tags").regex(keyword, "i")
        );

        Query query = new Query(criteria)
                .skip((long) page * size)
                .limit(size)
                .with(Sort.by(Sort.Direction.DESC, "createdAt"));

        return mongoTemplate.find(query, Note.class);
    }

    @Override
    public Mono<Long> countByKeyword(String keyword) {
        Query query = new Query();
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("title").regex(keyword, "i"),
                Criteria.where("content").regex(keyword, "i"),
                Criteria.where("tags").elemMatch(Criteria.where("$regex").regex(keyword, "i"))
        );
        query.addCriteria(criteria);

        return mongoTemplate.count(query, Note.class);
    }
}
