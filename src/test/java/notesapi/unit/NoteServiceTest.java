package notesapi.unit;

import notesapi.dtos.request.NoteRequest;
import notesapi.entities.Note;
import notesapi.exceptions.NoteNotFoundException;
import notesapi.repositories.NoteRepository;
import notesapi.services.NoteService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static notesapi.TestData.ANY_CONTENT;
import static notesapi.TestData.ANY_ID;
import static notesapi.TestData.ANY_OTHER_CONTENT;
import static notesapi.TestData.ANY_OTHER_TAG;
import static notesapi.TestData.ANY_OTHER_TITLE;
import static notesapi.TestData.ANY_TAG;
import static notesapi.TestData.ANY_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Nested
    class FindById {

        @Test
        public void should_find_note_by_id() {
            when(noteRepository.findById(ANY_ID)).thenReturn(Optional.of(new Note()));

            Note expectedNote = noteService.findById(ANY_ID);

            assertThat(expectedNote).isNotNull();
        }

        @Test
        public void should_throw_not_found_exception_when_note_does_not_exist() {
            String id = ANY_ID;
            when(noteRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteService.findById(id)).isInstanceOf(NoteNotFoundException.class);
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
    }

    @Nested
    class Create {

        @Test
        void should_return_new_note_when_creating_note() {
            NoteRequest request = new NoteRequest(ANY_TITLE, ANY_CONTENT, List.of(ANY_TAG));
            Note expectedNote = createNote();
            when(noteRepository.save(any())).thenReturn(expectedNote);

            Note createdNote = noteService.create(request);

            assertThat(createdNote).isEqualTo(expectedNote);
        }
    }

    @Nested
    class Update {

        @Test
        void should_return_updated_note_when_updating_note() {
            when(noteRepository.findById(ANY_ID)).thenReturn(Optional.of(createNote()));

            String updatedTitle = ANY_OTHER_TITLE;
            String updatedContent = ANY_OTHER_CONTENT;
            List<String> updatedTags = List.of(ANY_TAG, ANY_OTHER_TAG);
            Note updatedNote = Note.builder()
                    .id(ANY_ID)
                    .title(updatedTitle)
                    .content(updatedContent)
                    .tags(updatedTags)
                    .build();

            when(noteRepository.save(any())).thenReturn(updatedNote);

            NoteRequest request = new NoteRequest(updatedTitle, updatedContent, updatedTags);
            Note note = noteService.update(request, ANY_ID);

            assertThat(note.getTitle()).isEqualTo(updatedTitle);
            assertThat(note.getContent()).isEqualTo(updatedContent);
            assertThat(note.getTags()).usingRecursiveComparison().isEqualTo(updatedTags);
        }

        @Test
        public void should_throw_not_found_exception_when_note_does_not_exist() {
            String id = ANY_ID;
            NoteRequest request = new NoteRequest(ANY_TITLE, ANY_CONTENT, List.of(ANY_TAG));
            when(noteRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteService.update(request, id)).isInstanceOf(NoteNotFoundException.class);
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
            String id = ANY_ID;
            when(noteRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteService.deleteById(id)).isInstanceOf(NoteNotFoundException.class);
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
