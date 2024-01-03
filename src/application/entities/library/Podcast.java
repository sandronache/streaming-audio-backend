package application.entities.library;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The part of library that holds Podcasts
 */
@Getter
public final class Podcast {
    private String name;
    private String owner;
    private ArrayList<Episode> episodes;

    /**
     * Constructor
     * @param name
     * @param owner
     * @param episodes
     */
    public Podcast(final String name, final String owner,
                   final ArrayList<Episode> episodes) {
        this.name = name;
        this.owner = owner;
        this.episodes = episodes;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Podcast podcast = (Podcast) o;
        return Objects.equals(name, podcast.name)
                && Objects.equals(owner, podcast.owner)
                && Objects.equals(episodes, podcast.episodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, owner, episodes);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public void setEpisodes(final ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }
}
