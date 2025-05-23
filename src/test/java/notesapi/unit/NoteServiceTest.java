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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static notesapi.common.TestData.ANY_CONTENT;
import static notesapi.common.TestData.ANY_ID;
import static notesapi.common.TestData.ANY_OTHER_CONTENT;
import static notesapi.common.TestData.ANY_OTHER_TAG;
import static notesapi.common.TestData.ANY_OTHER_TITLE;
import static notesapi.common.TestData.ANY_TAG;
import static notesapi.common.TestData.ANY_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
            when(noteRepository.findById(ANY_ID)).thenReturn(Optional.of(createNote()));

            Note expectedNote = noteService.findById(ANY_ID);

            assertThat(expectedNote).isNotNull();
            assertThat(expectedNote.getId()).isEqualTo(ANY_ID);
            assertThat(expectedNote.getTitle()).isEqualTo(ANY_TITLE);
            assertThat(expectedNote.getContent()).isEqualTo(ANY_CONTENT);
            assertThat(expectedNote.getTags()).usingRecursiveComparison().isEqualTo(List.of(ANY_TAG));
            assertThat(expectedNote.getCreatedAt()).isNotNull();
            assertThat(expectedNote.getUpdatedAt()).isNotNull();
        }

        @Test
        public void should_throw_not_found_exception_when_note_does_not_exist() {
            when(noteRepository.findById(ANY_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteService.findById(ANY_ID)).isInstanceOf(NoteNotFoundException.class);
        }
    }

    @Nested
    class FindAll {

        @Test
        public void should_return_page_of_notes_when_getting_all_notes() {
            Note note = createNote();
            List<Note> notes = List.of(note);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Note> page = new PageImpl<>(notes, pageable, notes.size());
            when(noteRepository.findAll(pageable)).thenReturn(page);

            Page<Note> expectedPage = noteService.findAll(pageable);

            assertThat(expectedPage.getContent()).hasSize(1);
            assertThat(expectedPage.getTotalElements()).isEqualTo(1);
            assertThat(expectedPage.getContent()).containsExactly(note);
        }

        @Test
        public void should_return_empty_page_when_there_are_no_notes() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Note> page = new PageImpl<>(List.of(), pageable, 0);
            when(noteRepository.findAll(pageable)).thenReturn(page);

            Page<Note> expectedPage = noteService.findAll(pageable);

            assertThat(expectedPage.getContent()).isEmpty();
            assertThat(expectedPage.getTotalElements()).isEqualTo(0);
        }
    }

    @Nested
    class Search {

        @Test
        public void should_return_page_of_notes_when_searching_notes_by_keyword() {
            Note note = createNote();
            List<Note> notes = List.of(note);
            Pageable pageable = PageRequest.of(0, 10);
            Page<Note> page = new PageImpl<>(notes, pageable, notes.size());
            String keyword = "title";
            when(noteRepository.search(pageable, keyword)).thenReturn(page);

            Page<Note> expectedPage = noteService.search(pageable, keyword);

            assertThat(expectedPage.getContent()).hasSize(1);
            assertThat(expectedPage.getTotalElements()).isEqualTo(1);
            assertThat(expectedPage.getContent()).containsExactly(note);
        }

        @Test
        public void should_return_empty_page_when_there_are_no_results() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Note> page = new PageImpl<>(List.of(), pageable, 0);
            String keyword = "title";
            when(noteRepository.search(pageable, keyword)).thenReturn(page);

            Page<Note> expectedPage = noteService.search(pageable, keyword);

            assertThat(expectedPage.getContent()).isEmpty();
            assertThat(expectedPage.getTotalElements()).isEqualTo(0);
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
            noteService.create(noteToCreate);

            verify(noteRepository).save(noteCaptor.capture());
            Note savedNote = noteCaptor.getValue();
            assertThat(savedNote.getTitle()).isEqualTo(ANY_TITLE);
            assertThat(savedNote.getContent()).isEqualTo(ANY_CONTENT);
            assertThat(savedNote.getTags()).usingRecursiveComparison().isEqualTo(List.of(ANY_TAG));
            assertThat(savedNote.getCreatedAt()).isEqualTo(time);
            assertThat(savedNote.getUpdatedAt()).isEqualTo(time);
        }
    }

    @Nested
    class Update {

        @Test
        void should_update_note() {
            Note originalNote = createNote();
            LocalDateTime updatedTime = LocalDateTime.now();
            when(noteRepository.findById(ANY_ID)).thenReturn(Optional.of(originalNote));
            when(dateTimeProvider.now()).thenReturn(updatedTime);

            Note noteToUpdate = Note.builder()
                    .title(ANY_OTHER_TITLE)
                    .content(ANY_OTHER_CONTENT)
                    .tags(List.of(ANY_TAG, ANY_OTHER_TAG))
                    .build();
            noteService.update(noteToUpdate, ANY_ID);

            verify(noteRepository).save(noteCaptor.capture());
            Note savedNote = noteCaptor.getValue();
            assertThat(savedNote.getId()).isEqualTo(ANY_ID);
            assertThat(savedNote.getTitle()).isEqualTo(ANY_OTHER_TITLE);
            assertThat(savedNote.getContent()).isEqualTo(ANY_OTHER_CONTENT);
            assertThat(savedNote.getTags()).usingRecursiveComparison().isEqualTo(List.of(ANY_TAG, ANY_OTHER_TAG));
            assertThat(savedNote.getCreatedAt()).isEqualTo(originalNote.getCreatedAt());
            assertThat(savedNote.getUpdatedAt()).isEqualTo(updatedTime);
        }

        @Test
        public void should_throw_not_found_exception_when_note_does_not_exist() {
            String id = ANY_ID;
            Note noteToUpdate = Note.builder()
                    .title(ANY_TITLE)
                    .content(ANY_CONTENT)
                    .tags(List.of(ANY_TAG))
                    .build();
            when(noteRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteService.update(noteToUpdate, id)).isInstanceOf(NoteNotFoundException.class);
            verify(noteRepository, never()).save(any());
        }
    }

    @Nested
    class Delete {

        @Test
        public void should_delete_note_by_id() {
            when(noteRepository.findById(ANY_ID)).thenReturn(Optional.of(createNote()));

            noteService.deleteById(ANY_ID);

            verify(noteRepository).deleteById(ANY_ID);
        }

        @Test
        public void should_throw_not_found_exception_when_note_does_not_exist() {
            when(noteRepository.findById(ANY_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteService.deleteById(ANY_ID)).isInstanceOf(NoteNotFoundException.class);
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
