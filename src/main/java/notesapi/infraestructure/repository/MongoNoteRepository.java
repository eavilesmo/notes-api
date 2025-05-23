package notesapi.infraestructure.repository;

import lombok.AllArgsConstructor;
import notesapi.domain.model.Note;
import notesapi.domain.repository.NoteRepository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor
public class MongoNoteRepository implements NoteRepository {

    private SpringDataNoteRepository springDataNoteRepository;

    @Override
    public Mono<Note> findById(String id) {
        return springDataNoteRepository.findById(id);
    }

    @Override
    public Mono<Note> save(Note note) {
        return springDataNoteRepository.save(note);
    }

    @Override
    public Mono<Void> deleteAll() {
        return springDataNoteRepository.deleteAll();
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return springDataNoteRepository.deleteById(id);
    }

    @Override
    public Flux<Note> findAll() {
        return springDataNoteRepository.findAll();
    }

    @Override
    public Flux<Note> search(String keyword) {
        return springDataNoteRepository.searchByKeyword(keyword);
    }
}
