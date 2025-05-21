package notesapi.unit;

import notesapi.entities.Note;
import notesapi.exception.NoteNotFoundException;
import notesapi.repositories.NoteRepository;
import notesapi.services.NoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
}
