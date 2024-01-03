package application.entities.library.users.host;

import lombok.Getter;

import java.util.Objects;

/**
 * Class for an announcement
 */
@Getter
public final class Announcement {
    private String name;
    private String description;

    /**
     * Constructor
     * @param name
     * @param description
     */
    public Announcement(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Announcement that = (Announcement) o;
        return Objects.equals(name, that.name)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

}
