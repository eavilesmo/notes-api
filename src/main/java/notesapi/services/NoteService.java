package notesapi.services;

import lombok.AllArgsConstructor;
import notesapi.entities.Note;
import notesapi.exceptions.NoteNotFoundException;
import notesapi.repositories.NoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class NoteService {

    private NoteRepository noteRepository;

    public Note findById(String id) {
        return noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }

    public Page<Note> findAll(Pageable pageable) {
        return noteRepository.findAll(pageable);
    }

    public Page<Note> search(Pageable pageable, String keyword) {
        return noteRepository.search(pageable, keyword);
    }

    public Note create(Note note) {
        Note noteToSave = Note.builder()
                .title(note.getTitle())
                .content(note.getContent())
                .tags(note.getTags())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return noteRepository.save(noteToSave);
    }

    public Note update(Note note, String id) {
        Note savedNote = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        Note updatedNote = Note.builder()
                .id(savedNote.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .tags(note.getTags())
                .createdAt(savedNote.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        return noteRepository.save(updatedNote);
    }

    public void deleteById(String id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
        noteRepository.deleteById(note.getId());
    }
}
