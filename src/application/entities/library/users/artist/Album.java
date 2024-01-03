package application.entities.library.users.artist;

import application.entities.library.Song;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class for an Album
 */
@Getter
public final class Album {
    private String name;
    private Integer releaseYear;
    private String description;
    private ArrayList<Song> songs;

    /**
     * Constructor
     * @param name
     * @param releaseYear
     * @param description
     * @param songs
     */
    public Album(final String name, final Integer releaseYear,
                 final String description, final ArrayList<Song> songs) {
        this.name = name;
        this.releaseYear = releaseYear;
        this.description = description;
        this.songs = songs;
    }

    /**
     * Calculates the total of likes for an album
     * @return
     */
    public int likesPerAlbum() {
        int result = 0;
        for (Song song: songs) {
            result += song.getLikes();
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Album album = (Album) o;
        return Objects.equals(name, album.name)
                && Objects.equals(releaseYear, album.releaseYear)
                && Objects.equals(description, album.description)
                && Objects.equals(songs, album.songs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, releaseYear, description, songs);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setReleaseYear(final Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }
}
