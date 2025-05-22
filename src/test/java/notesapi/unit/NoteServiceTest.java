package notesapi.unit;

import notesapi.dtos.request.NoteRequest;
import notesapi.entities.Note;
import notesapi.exceptions.NoteNotFoundException;
import notesapi.repositories.NoteRepository;
import notesapi.services.NoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

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

    @Test
    public void should_find_note_by_id() {
        when(noteRepository.findById("any_id")).thenReturn(Optional.of(new Note()));

        Note expectedNote = noteService.findById("any_id");

        assertThat(expectedNote).isNotNull();
    }

    @Test
    public void should_throw_not_found_exception_when_note_does_not_exist() {
        String id = "any_id";
        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.findById(id)).isInstanceOf(NoteNotFoundException.class);
    }

    @Test
    public void should_return_a_page_of_notes_when_getting_all_notes() {
        Note note1 = new Note();
        note1.setId("any_id");
        note1.setTitle("any title");
        note1.setContent("any content");
        List<Note> notes = List.of(note1);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Note> page = new PageImpl<>(notes, pageable, notes.size());
        when(noteRepository.findAll(pageable)).thenReturn(page);

        Page<Note> expectedPage = noteService.findAll(pageable);

        assertThat(expectedPage.getContent()).hasSize(1);
        assertThat(expectedPage.getTotalElements()).isEqualTo(1);
        assertThat(expectedPage.getContent()).containsExactly(note1);
    }

    @Test
    public void should_return_a_list_of_notes_when_searching_notes_by_keyword() {
        Note note1 = new Note();
        note1.setTitle("title 1");
        String keyword = "title";
        when(noteRepository.search(keyword)).thenReturn(List.of(note1));

        List<Note> expectedNotes = noteService.search(keyword);

        assertThat(expectedNotes).isEqualTo(List.of(note1));
    }

    @Test
    void should_return_a_new_note_when_creating_a_note() {
        NoteRequest request = new NoteRequest("title", "content", List.of("tag1", "tag2"));
        Note expectedNote = new Note();
        expectedNote.setTitle(request.getTitle());
        expectedNote.setContent(request.getContent());
        expectedNote.setTags(request.getTags());
        when(noteRepository.save(any())).thenReturn(expectedNote);

        Note createdNote = noteService.create(request);

        assertThat(createdNote).isEqualTo(expectedNote);
    }

    @Test
    void should_return_updated_note_when_updating_a_note() {
        Note note = new Note();
        note.setId("any_id");
        note.setTitle("old title");
        note.setContent("old content");
        when(noteRepository.findById("any_id")).thenReturn(Optional.of(note));

        String updatedTitle = "new title";
        String updatedContent = "new content";
        List<String> updatedTags = List.of("updated-tag1", "updated-tag2");

        note.setTitle(updatedTitle);
        note.setContent(updatedContent);
        note.setTags(updatedTags);
        when(noteRepository.save(any())).thenReturn(note);

        NoteRequest request = new NoteRequest(updatedTitle, updatedContent, updatedTags);
        Note updatedNote = noteService.update(request, "any_id");

        assertThat(updatedNote.getTitle()).isEqualTo(updatedTitle);
        assertThat(updatedNote.getContent()).isEqualTo(updatedContent);
        assertThat(updatedNote.getTags()).usingRecursiveComparison().isEqualTo(updatedTags);
    }

    @Test
    public void should_throw_not_found_exception_when_updating_unexistent_note() {
        String id = "any_id";
        NoteRequest request = new NoteRequest("any-title", "any-content", List.of("any-tag"));
        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.update(request, id)).isInstanceOf(NoteNotFoundException.class);
    }

    @Test
    public void should_delete_note_by_id() {
        Note savedNote = new Note();
        savedNote.setId("any_id");
        when(noteRepository.findById("any_id")).thenReturn(Optional.of(savedNote));

        noteService.deleteById("any_id");

        verify(noteRepository).deleteById("any_id");
    }

    @Test
    public void should_throw_not_found_exception_when_deleting_unexistent_note() {
        String id = "any_id";
        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.deleteById(id)).isInstanceOf(NoteNotFoundException.class);
    }
}
