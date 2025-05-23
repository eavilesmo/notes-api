package notesapi.domain.service;

import lombok.AllArgsConstructor;
import notesapi.domain.model.Note;
import notesapi.domain.exception.NoteNotFoundException;
import notesapi.domain.repository.NoteRepository;
import notesapi.common.DateTimeProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class NoteService {

    private NoteRepository noteRepository;
    private DateTimeProvider dateTimeProvider;

    public Mono<Note> findById(String id) {
        return noteRepository.findById(id).switchIfEmpty(Mono.error(new NoteNotFoundException(id)));
    }

    public Flux<Note> findAll(int page, int size) {
        return noteRepository.findAll(page, size)
                .skip((long) page * size)
                .take(size);
    }

    public Flux<Note> search(String keyword, int page, int size) {
        return noteRepository.search(keyword, page, size)
                .skip((long) page * size)
                .take(size);
    }

    public Mono<Note> create(Note note) {
        Note noteToSave = Note.builder()
                .title(note.getTitle())
                .content(note.getContent())
                .tags(note.getTags())
                .createdAt(dateTimeProvider.now())
                .updatedAt(dateTimeProvider.now())
                .build();
        return noteRepository.save(noteToSave);
    }

    public Mono<Note> update(Note note, String id) {
        return noteRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoteNotFoundException(id)))
                .map(savedNote -> Note.builder()
                        .id(savedNote.getId())
                        .title(note.getTitle())
                        .content(note.getContent())
                        .tags(note.getTags())
                        .createdAt(savedNote.getCreatedAt())
                        .updatedAt(dateTimeProvider.now())
                        .build())
                .flatMap(noteRepository::save);
    }

    public Mono<Void> deleteById(String id) {
        return noteRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoteNotFoundException(id)))
                .flatMap(note -> noteRepository.deleteById(note.getId()));
    }

    public Mono<Long> count() {
        return noteRepository.count();
    }

    public Mono<Long> countByKeyword(String keyword) {
        return noteRepository.countByKeyword(keyword);
    }
}
