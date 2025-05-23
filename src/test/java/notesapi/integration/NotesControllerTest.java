package notesapi.integration;

import notesapi.application.dto.request.NoteRequest;
import notesapi.application.dto.response.NoteResponse;
import notesapi.domain.model.Note;
import notesapi.domain.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotesControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll().block();
    }

    public static final String NOTE_NOT_FOUND_ERROR_MESSAGE = "Note with ID non-existent-id not found.";

    @Nested
    class GetNoteById {

        @Test
        void should_return_ok_when_getting_note_by_id() {
            Note note = createNote();
            noteRepository.save(note).block();

            webTestClient.get()
                    .uri("/notes/{id}", ANY_ID)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(NoteResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        assertThat(response.id()).isEqualTo(ANY_ID);
                        assertThat(response.title()).isEqualTo(ANY_TITLE);
                        assertThat(response.content()).isEqualTo(ANY_CONTENT);
                        assertThat(response.tags()).usingRecursiveComparison().isEqualTo(List.of(ANY_TAG));
                        assertThat(response.createdAt()).isInstanceOf(LocalDateTime.class);
                        assertThat(response.updatedAt()).isInstanceOf(LocalDateTime.class);
                    });
        }

        @Test
        void should_return_not_found_when_note_does_not_exist() {
            webTestClient.get()
                    .uri("/notes/non-existent-id")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(String.class)
                    .value(body -> assertThat(body).contains(NOTE_NOT_FOUND_ERROR_MESSAGE));
        }
    }

    @Nested
    class GetAllNotes {

        @Test
        void should_return_ok_when_getting_all_notes() {
            Note savedNote = noteRepository.save(createNote()).block();

            webTestClient.get()
                    .uri("/notes?page=0&size=10")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.items.length()").isEqualTo(1)
                    .jsonPath("$.items[0].id").isEqualTo(savedNote.getId())
                    .jsonPath("$.currentPage").isEqualTo(0)
                    .jsonPath("$.pageSize").isEqualTo(10)
                    .jsonPath("$.totalItems").isEqualTo(1)
                    .jsonPath("$.totalPages").isEqualTo(1);
        }

        @Test
        void should_return_ok_when_there_are_no_notes() {
            webTestClient.get()
                    .uri("/notes?page=0&size=10")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.items.length()").isEqualTo(0)
                    .jsonPath("$.currentPage").isEqualTo(0)
                    .jsonPath("$.pageSize").isEqualTo(10)
                    .jsonPath("$.totalItems").isEqualTo(0)
                    .jsonPath("$.totalPages").isEqualTo(0);
        }
    }

    @Nested
    class SearchNotesByKeyword {

        @Test
        void should_return_ok_when_searching_notes_by_keyword() {
            noteRepository.save(createNote()).block();

            String keyword = "title";
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/notes/search")
                            .queryParam("keyword", keyword)
                            .queryParam("page", 0)
                            .queryParam("size", 10)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.items.length()").isEqualTo(1)
                    .jsonPath("$.currentPage").isEqualTo(0)
                    .jsonPath("$.pageSize").isEqualTo(10)
                    .jsonPath("$.totalItems").isEqualTo(1)
                    .jsonPath("$.totalPages").isEqualTo(1);
        }

        @Test
        void should_return_ok_when_there_are_no_results() {
            String keyword = "title";
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/notes/search")
                            .queryParam("keyword", keyword)
                            .queryParam("page", 0)
                            .queryParam("size", 10)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.items.length()").isEqualTo(0)
                    .jsonPath("$.currentPage").isEqualTo(0)
                    .jsonPath("$.pageSize").isEqualTo(10)
                    .jsonPath("$.totalItems").isEqualTo(0)
                    .jsonPath("$.totalPages").isEqualTo(0);
        }
    }

    @Nested
    class CreateNote {

        @Test
        void should_return_ok_when_creating_note() {
            String title = ANY_TITLE;
            String content = ANY_CONTENT;
            List<String> tags = List.of(ANY_TAG);
            NoteRequest request = new NoteRequest(title, content, tags);

            webTestClient.post()
                    .uri("/notes")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(NoteResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();
                        Note savedNote = noteRepository.findById(response.id()).block();
                        assertThat(savedNote).isNotNull();
                        assertThat(savedNote.getTitle()).isEqualTo(title);
                        assertThat(savedNote.getContent()).isEqualTo(content);
                        assertThat(savedNote.getTags()).usingRecursiveComparison().isEqualTo(tags);
                        assertThat(savedNote.getCreatedAt()).isInstanceOf(LocalDateTime.class);
                        assertThat(savedNote.getUpdatedAt()).isInstanceOf(LocalDateTime.class);
                    });
        }
    }

    @Nested
    class UpdateNote {
        @Test
        void should_return_ok_when_updating_note() {
            noteRepository.save(createNote()).block();

            List<String> updatedTags = List.of(ANY_TAG, ANY_OTHER_TAG);
            NoteRequest request = new NoteRequest(ANY_OTHER_TITLE, ANY_OTHER_CONTENT, updatedTags);

            webTestClient.put()
                    .uri("/notes/" + ANY_ID)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(NoteResponse.class)
                    .value(response -> {
                        assertThat(response).isNotNull();

                        Note updatedNote = noteRepository.findById(ANY_ID).block();
                        assertThat(updatedNote).isNotNull();
                        assertThat(updatedNote.getTitle()).isEqualTo(ANY_OTHER_TITLE);
                        assertThat(updatedNote.getContent()).isEqualTo(ANY_OTHER_CONTENT);
                        assertThat(updatedNote.getTags()).usingRecursiveComparison().isEqualTo(updatedTags);
                        assertThat(updatedNote.getCreatedAt()).isInstanceOf(LocalDateTime.class);
                        assertThat(updatedNote.getUpdatedAt()).isInstanceOf(LocalDateTime.class);
                    });
        }

        @Test
        void should_return_not_found_when_note_does_not_exist() {
            NoteRequest request = new NoteRequest(ANY_TITLE, ANY_CONTENT, List.of(ANY_TAG));

            webTestClient.put()
                    .uri("/notes/non-existent-id")
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(String.class)
                    .value(body -> assertThat(body).contains(NOTE_NOT_FOUND_ERROR_MESSAGE));
        }
    }

    @Nested
    class DeleteNote {

        @Test
        void should_return_ok_when_deleting_note_by_id() {
            Note savedNote = noteRepository.save(createNote()).block();

            webTestClient.delete()
                    .uri("/notes/{id}", savedNote.getId())
                    .exchange()
                    .expectStatus().isNoContent()
                    .expectBody().isEmpty();

            Optional<Note> deletedNote = noteRepository.findById(savedNote.getId()).blockOptional();
            assertThat(deletedNote).isEmpty();
        }

        @Test
        void should_return_not_found_when_note_does_not_exist() {
            webTestClient.delete()
                    .uri("/notes/{id}", "non-existent-id")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(String.class)
                    .value(body -> assertThat(body).contains(NOTE_NOT_FOUND_ERROR_MESSAGE));
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
