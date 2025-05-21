package notesapi.unit;

import notesapi.entities.Note;
import notesapi.repositories.NoteRepository;
import notesapi.services.NoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

        Optional<Note> expectedNote = noteService.findById("any_id");

        assertThat(expectedNote.get()).isNotNull();
    }
}
