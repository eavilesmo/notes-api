package notesapi.domain.repository;

import notesapi.domain.model.Note;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NoteRepository {

    Mono<Note> findById(String id);
    Mono<Note> save(Note note);
    Mono<Void> deleteAll();
    Mono<Void> deleteById(String id);
    Flux<Note> findAll();
    Flux<Note> search(String keyword);
}
