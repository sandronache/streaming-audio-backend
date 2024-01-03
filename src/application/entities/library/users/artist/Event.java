package application.entities.library.users.artist;

import lombok.Getter;

import java.util.Objects;

/**
 * Class for an event
 */
@Getter
public final class Event {
    private String name;
    private String description;
    private String date;

    /**
     * Constructor
     * @param name
     * @param description
     * @param date
     */
    public Event(final String name, final String description,
                 final String date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return Objects.equals(name, event.name)
                && Objects.equals(description, event.description)
                && Objects.equals(date, event.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, date);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setDate(final String date) {
        this.date = date;
    }
}
