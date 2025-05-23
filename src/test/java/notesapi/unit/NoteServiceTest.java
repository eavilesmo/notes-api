package notesapi.unit;

import notesapi.domain.model.Note;
import notesapi.domain.exception.NoteNotFoundException;
import notesapi.domain.repository.NoteRepository;
import notesapi.application.service.NoteService;
import notesapi.application.common.DateTimeProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static notesapi.common.TestData.ANY_CONTENT;
import static notesapi.common.TestData.ANY_ID;
import static notesapi.common.TestData.ANY_OTHER_CONTENT;
import static notesapi.common.TestData.ANY_OTHER_TAG;
import static notesapi.common.TestData.ANY_OTHER_TITLE;
import static notesapi.common.TestData.ANY_TAG;
import static notesapi.common.TestData.ANY_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;
    @Mock
    private DateTimeProvider dateTimeProvider;
    @InjectMocks
    private NoteService noteService;

    @Captor
    ArgumentCaptor<Note> noteCaptor;

    @Nested
    class FindById {

        @Test
        public void should_find_note_by_id() {
            Note note = createNote();
            when(noteRepository.findById(ANY_ID)).thenReturn(Mono.just(note));

            Mono<Note> resultMono = noteService.findById(ANY_ID);

            StepVerifier.create(resultMono)
                    .assertNext(foundNote -> {
                        assertThat(foundNote).isNotNull();
                        assertThat(foundNote.getId()).isEqualTo(ANY_ID);
                        assertThat(foundNote.getTitle()).isEqualTo(ANY_TITLE);
                        assertThat(foundNote.getContent()).isEqualTo(ANY_CONTENT);
                        assertThat(foundNote.getTags()).usingRecursiveComparison().isEqualTo(List.of(ANY_TAG));
                        assertThat(foundNote.getCreatedAt()).isNotNull();
                        assertThat(foundNote.getUpdatedAt()).isNotNull();
                    })
                    .verifyComplete();
        }

        @Test
        public void should_throw_not_found_exception_when_note_does_not_exist() {
            when(noteRepository.findById(ANY_ID)).thenReturn(Mono.empty());

            Mono<Note> resultMono = noteService.findById(ANY_ID);

            StepVerifier.create(resultMono)
                    .expectError(NoteNotFoundException.class)
                    .verify();
        }
    }

    @Nested
    class FindAll {

        @Test
        void should_return_all_notes_paginated() {
            List<Note> notes = List.of(createNote());
            int page = 0;
            int size = 10;
            when(noteRepository.findAll(page, size)).thenReturn(Flux.fromIterable(notes));

            Flux<Note> notesFlux = noteService.findAll(page, size);

            StepVerifier.create(notesFlux)
                    .expectNextSequence(notes)
                    .verifyComplete();
        }

        @Test
        void should_return_empty_paginated_when_there_are_no_notes() {
            int page = 0;
            int size = 10;
            when(noteRepository.findAll(page, size)).thenReturn(Flux.empty());

            Flux<Note> notesFlux = noteService.findAll(page, size);

            StepVerifier.create(notesFlux)
                    .expectComplete()
                    .verify();
        }
    }

    @Nested
    class Search {

        @Test
        void should_return_notes_when_searching_by_keyword_paginated() {
            List<Note> notes = List.of(createNote());
            String keyword = "title";
            int page = 0;
            int size = 10;
            when(noteRepository.search(keyword, page, size)).thenReturn(Flux.fromIterable(notes));

            Flux<Note> notesFlux = noteService.search(keyword, page, size);

            StepVerifier.create(notesFlux)
                    .expectNextSequence(notes)
                    .verifyComplete();
        }

        @Test
        void should_return_empty_when_no_search_results_paginated() {
            String keyword = "title";
            int page = 0;
            int size = 10;
            when(noteRepository.search(keyword, page, size)).thenReturn(Flux.empty());

            Flux<Note> notesFlux = noteService.search(keyword, page, size);

            StepVerifier.create(notesFlux)
                    .expectComplete()
                    .verify();
        }
    }

    @Nested
    class Create {

    @Test
    void should_create_note() {
        LocalDateTime time = LocalDateTime.now();
        when(dateTimeProvider.now()).thenReturn(time);

        Note noteToCreate = Note.builder()
                .title(ANY_TITLE)
                .content(ANY_CONTENT)
                .tags(List.of(ANY_TAG))
                .build();

        when(noteRepository.save(noteCaptor.capture())).thenAnswer(invocation -> {
            Note argNote = invocation.getArgument(0);
            return Mono.just(argNote);
        });

        Mono<Note> resultMono = noteService.create(noteToCreate);

        StepVerifier.create(resultMono)
                .assertNext(savedNote -> {
                    assertThat(savedNote.getTitle()).isEqualTo(ANY_TITLE);
                    assertThat(savedNote.getContent()).isEqualTo(ANY_CONTENT);
                    assertThat(savedNote.getTags()).usingRecursiveComparison().isEqualTo(List.of(ANY_TAG));
                    assertThat(savedNote.getCreatedAt()).isEqualTo(time);
                    assertThat(savedNote.getUpdatedAt()).isEqualTo(time);
                })
                .verifyComplete();

        Note savedNote = noteCaptor.getValue();
        assertThat(savedNote.getCreatedAt()).isEqualTo(time);
        assertThat(savedNote.getUpdatedAt()).isEqualTo(time);
    }
    }

    @Nested
    class Update {

        @Test
        void should_update_note() {
            Note originalNote = createNote();
            when(noteRepository.findById(ANY_ID)).thenReturn(Mono.just(originalNote));
            LocalDateTime updatedTime = LocalDateTime.now();
            when(dateTimeProvider.now()).thenReturn(updatedTime);
            when(noteRepository.save(noteCaptor.capture())).thenAnswer(invocation -> {
                Note argNote = invocation.getArgument(0);
                return Mono.just(argNote);
            });

            Note noteToUpdate = Note.builder()
                    .title(ANY_OTHER_TITLE)
                    .content(ANY_OTHER_CONTENT)
                    .tags(List.of(ANY_TAG, ANY_OTHER_TAG))
                    .build();
            Mono<Note> updatedNoteMono = noteService.update(noteToUpdate, ANY_ID);

            StepVerifier.create(updatedNoteMono)
                    .assertNext(savedNote -> {
                        assertThat(savedNote.getId()).isEqualTo(ANY_ID);
                        assertThat(savedNote.getTitle()).isEqualTo(ANY_OTHER_TITLE);
                        assertThat(savedNote.getContent()).isEqualTo(ANY_OTHER_CONTENT);
                        assertThat(savedNote.getTags()).usingRecursiveComparison().isEqualTo(List.of(ANY_TAG, ANY_OTHER_TAG));
                        assertThat(savedNote.getCreatedAt()).isEqualTo(originalNote.getCreatedAt());
                        assertThat(savedNote.getUpdatedAt()).isEqualTo(updatedTime);
                    })
                    .verifyComplete();

            Note savedNoteCaptured = noteCaptor.getValue();
            assertThat(savedNoteCaptured.getUpdatedAt()).isEqualTo(updatedTime);
        }

        @Test
        void should_throw_not_found_exception_when_note_does_not_exist() {
            Note noteToUpdate = Note.builder()
                    .title(ANY_TITLE)
                    .content(ANY_CONTENT)
                    .tags(List.of(ANY_TAG))
                    .build();
            when(noteRepository.findById(ANY_ID)).thenReturn(Mono.empty());

            Mono<Note> updatedNoteMono = noteService.update(noteToUpdate, ANY_ID);

            StepVerifier.create(updatedNoteMono)
                    .expectError(NoteNotFoundException.class)
                    .verify();
            verify(noteRepository, never()).save(any());
        }
    }

    @Nested
    class Delete {

        @Test
        void should_delete_note_by_id() {
            when(noteRepository.findById(ANY_ID)).thenReturn(Mono.just(createNote()));
            when(noteRepository.deleteById(ANY_ID)).thenReturn(Mono.empty());

            Mono<Void> deleteMono = noteService.deleteById(ANY_ID);

            StepVerifier.create(deleteMono)
                    .verifyComplete();
            verify(noteRepository).deleteById(ANY_ID);
        }

        @Test
        void should_throw_not_found_exception_when_note_does_not_exist() {
            when(noteRepository.findById(ANY_ID)).thenReturn(Mono.empty());

            Mono<Void> deleteMono = noteService.deleteById(ANY_ID);

            StepVerifier.create(deleteMono)
                    .expectError(NoteNotFoundException.class)
                    .verify();
            verify(noteRepository, never()).deleteById(any());
        }
    }

    private Note createNote() {
        return Note.builder()
                .id(ANY_ID)
                .title(ANY_TITLE)
                .content(ANY_CONTENT)
                .tags(List.of(ANY_TAG))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
