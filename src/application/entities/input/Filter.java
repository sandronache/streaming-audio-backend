package application.entities.input;

import lombok.Getter;

import java.util.ArrayList;

/**
 * Filter class contains all possible fields for filters input
 */
@Getter
public final class Filter {
    private String name;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private String releaseYear;
    private String artist;
    private String owner;
    private String description;

    public void setName(final String name) {
        this.name = name;
    }

    public void setAlbum(final String album) {
        this.album = album;
    }

    public void setTags(final ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setLyrics(final String lyrics) {
        this.lyrics = lyrics;
    }

    public void setGenre(final String genre) {
        this.genre = genre;
    }

    public void setReleaseYear(final String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
