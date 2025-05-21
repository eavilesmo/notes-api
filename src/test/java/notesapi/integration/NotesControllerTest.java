package notesapi.integration;

import notesapi.entities.Note;
import notesapi.services.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotesControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void should_return_note_when_getting_note_by_id() {
        ResponseEntity<Note> response = restTemplate.getForEntity("/notes/any_id", Note.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
