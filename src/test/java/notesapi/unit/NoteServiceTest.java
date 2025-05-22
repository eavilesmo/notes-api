package notesapi.unit;

import notesapi.dto.NoteCreateRequest;
import notesapi.entities.Note;
import notesapi.exception.NoteNotFoundException;
import notesapi.repositories.NoteRepository;
import notesapi.services.NoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    public void should_return_a_list_of_notes_when_getting_all_notes() {
        List<Note> savedNotes = List.of(new Note(), new Note());
        when(noteRepository.findAll()).thenReturn(savedNotes);

        List<Note> expectedNotes = noteService.findAll();

        assertThat(expectedNotes).usingRecursiveComparison().isEqualTo(savedNotes);
    }

    @Test
    void should_return_a_new_note_when_creating_a_note() {
        NoteCreateRequest request = new NoteCreateRequest("title", "content");
        Note expectedNote = new Note();
        expectedNote.setTitle(request.getTitle());
        expectedNote.setContent(request.getContent());
        when(noteRepository.save(any())).thenReturn(expectedNote);

        Note createdNote = noteService.create(request);

        assertThat(createdNote).isEqualTo(expectedNote);
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
