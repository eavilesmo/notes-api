package notesapi.integration;

import notesapi.dtos.request.NoteRequest;
import notesapi.dtos.response.NoteResponse;
import notesapi.dtos.response.PaginatedResponse;
import notesapi.entities.Note;
import notesapi.repositories.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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

    @Nested
    class GetNoteById {

        @Test
        void should_return_ok_when_getting_note_by_id() {
            Note savedNote = noteRepository.save(new Note());

            ResponseEntity<NoteResponse> response = restTemplate.getForEntity("/notes/" + savedNote.getId(), NoteResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
        }

        @Test
        void should_return_not_found_when_note_does_not_exist() {
            ResponseEntity<String> response = restTemplate.getForEntity("/notes/non-existent-id", String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).contains("Note with ID non-existent-id not found.");
        }
    }

    @Nested
    class GetAllNotes {

        @Test
        void should_return_ok_when_getting_all_notes() {
            Note note1 = new Note();
            note1.setTitle("title 1");
            Note note2 = new Note();
            note2.setContent("any content title");
            noteRepository.save(note1);
            noteRepository.save(note2);

            ResponseEntity<PaginatedResponse> response = restTemplate.exchange(
                    "/notes",
                    HttpMethod.GET,
                    null,
                    PaginatedResponse.class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getItems()).hasSize(2);
        }
    }

    @Nested
    class SearchNotesByKeyword {
        @Test
        void should_return_ok_when_searching_notes_by_keyword() {
            Note note1 = new Note();
            note1.setTitle("title 1");
            Note note2 = new Note();
            note2.setContent("any content title");
            noteRepository.save(note1);
            noteRepository.save(note2);
            noteRepository.save(new Note());

            String keyword = "title";
            ResponseEntity<PaginatedResponse> response = restTemplate.exchange(
                    "/notes/search?keyword=" + keyword,
                    HttpMethod.GET,
                    null,
                    PaginatedResponse.class
            );

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getItems()).hasSize(2);
        }
    }

    @Nested
    class CreateNote {
        @Test
        void should_return_ok_when_creating_note() {
            String title = "title";
            String content = "content";
            List<String> tags = List.of("tag1", "tag2");
            NoteRequest request = new NoteRequest(title, content, tags);

            ResponseEntity<NoteResponse> response = restTemplate.postForEntity("/notes", request, NoteResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            Optional<Note> savedNote = noteRepository.findById(response.getBody().id());
            assertThat(savedNote).isPresent();
            assertThat(savedNote.get().getTitle()).isEqualTo(title);
            assertThat(savedNote.get().getContent()).isEqualTo(content);
            assertThat(savedNote.get().getTags()).usingRecursiveComparison().isEqualTo(tags);
            assertThat(savedNote.get().getCreatedAt()).isNotNull();
            assertThat(savedNote.get().getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    class UpdateNote {
        @Test
        void should_return_ok_when_updating_note() {
            Note note = new Note();
            note.setTitle("old title");
            note.setContent("old content");
            noteRepository.save(note);

            String updatedTitle = "new title";
            String updatedContent = "new content";
            List<String> updatedTags = List.of("updated-tag1", "updated-tag2");
            NoteRequest request = new NoteRequest(updatedTitle, updatedContent, updatedTags);

            ResponseEntity<NoteResponse> response = restTemplate.exchange("/notes/" + note.getId(), HttpMethod.PUT, new HttpEntity<>(request), NoteResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();

            Optional<Note> updatedNote = noteRepository.findById(note.getId());
            assertThat(updatedNote).isPresent();
            assertThat(updatedNote.get().getTitle()).isEqualTo(updatedTitle);
            assertThat(updatedNote.get().getContent()).isEqualTo(updatedContent);
            assertThat(updatedNote.get().getTags()).usingRecursiveComparison().isEqualTo(updatedTags);
            assertThat(updatedNote.get().getCreatedAt().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(note.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
            assertThat(updatedNote.get().getUpdatedAt().truncatedTo(ChronoUnit.MILLIS)).isNotEqualTo(note.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS));
        }

        @Test
        void should_return_not_found_when_note_does_not_exist() {
            NoteRequest request = new NoteRequest("any-title", "any-content", List.of("any-tag"));
            ResponseEntity<String> response = restTemplate.exchange("/notes/non-existent-id" , HttpMethod.PUT, new HttpEntity<>(request), String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).contains("Note with ID non-existent-id not found.");
        }
    }

    @Nested
    class DeleteNote {
        @Test
        void should_return_ok_when_deleting_note_by_id() {
            Note savedNote = noteRepository.save(new Note());
            ResponseEntity<Void> response = restTemplate.exchange("/notes/" + savedNote.getId(), HttpMethod.DELETE, null, Void.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNull();
            assertThat(noteRepository.findById(savedNote.getId())).isEmpty();
        }

        @Test
        void should_return_not_found_when_note_does_not_exist() {
            ResponseEntity<String> response = restTemplate.exchange("/notes/non-existent-id" , HttpMethod.DELETE, null, String.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).contains("Note with ID non-existent-id not found.");
        }
    }
}
