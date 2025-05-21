package notesapi.exception;

public class NoteNotFoundException extends RuntimeException{
    public NoteNotFoundException(String id) {
        super("Note with ID " + id + " not found.");
    }
}
