package notesapi.integration;

import notesapi.dto.request.NoteCreateRequest;
import notesapi.entities.Note;
import notesapi.repositories.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotesControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
    }

    @Test
    void should_return_ok_when_getting_note_by_id() {
        Note savedNote = noteRepository.save(new Note());

        ResponseEntity<Note> response = restTemplate.getForEntity("/notes/" + savedNote.getId(), Note.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void should_return_not_found_when_note_does_not_exist() {
        ResponseEntity<String> response = restTemplate.getForEntity("/notes/non-existent-id", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Note with ID non-existent-id not found.");
    }

    @Test
    void should_return_ok_when_getting_all_notes() {
        noteRepository.save(new Note());
        noteRepository.save(new Note());

        ResponseEntity<List<Note>> response = restTemplate.exchange(
                "/notes",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Note>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);
    }

    @Test
    void should_return_created_when_creating_a_note() {
        String title = "title";
        String content = "content";
        NoteCreateRequest request = new NoteCreateRequest(title, content);

        ResponseEntity<Note> response = restTemplate.postForEntity("/notes", request, Note.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Note note = response.getBody();
        assertThat(note.getTitle()).isEqualTo(title);
        assertThat(note.getContent()).isEqualTo(content);
        assertThat(note.getId()).isNotNull();
        assertThat(note.getCreatedAt()).isNotNull();
        assertThat(note.getUpdatedAt()).isNotNull();

        List<Note> savedNotes = noteRepository.findAll();
        assertThat(savedNotes).hasSize(1);
        assertThat(savedNotes.getFirst().getTitle()).isEqualTo("title");
    }


    @Test
    void should_return_ok_when_deleting_note_by_id() {
        Note savedNote = noteRepository.save(new Note());
        ResponseEntity<Void> response = restTemplate.exchange("/notes/" + savedNote.getId(), HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
        assertThat(noteRepository.findById(savedNote.getId())).isEmpty();
    }

    @Test
    void should_return_not_found_when_deleting_unexistent_note() {
        ResponseEntity<String> response = restTemplate.exchange("/notes/non-existent-id" , HttpMethod.DELETE, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Note with ID non-existent-id not found.");
    }
}
