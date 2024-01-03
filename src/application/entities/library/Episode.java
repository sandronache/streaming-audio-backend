package application.entities.library;

import lombok.Getter;

import java.util.Objects;

/**
 * This holds an episode from a podcast
 */
@Getter
public final class Episode {
    private String name;
    private Integer duration;
    private String description;

    /**
     * Default constructor
     */
    public Episode() { }

    /**
     * Constructor
     * @param name
     * @param duration
     * @param description
     */
    public Episode(final String name, final Integer duration,
                   final String description) {
        this.name = name;
        this.duration = duration;
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
        Episode episode = (Episode) o;
        return Objects.equals(name, episode.name)
                && Objects.equals(duration, episode.duration)
                && Objects.equals(description, episode.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, duration, description);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
