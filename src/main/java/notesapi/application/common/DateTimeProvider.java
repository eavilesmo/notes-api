package notesapi.application.common;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeProvider {

    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}
