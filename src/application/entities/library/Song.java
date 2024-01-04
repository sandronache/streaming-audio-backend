package application.entities.library;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This holds a song from the library
 */
@Getter
public final class Song {
    private String name;
    private Integer duration;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private Integer releaseYear;
    private String artist;
    private Integer likes;
    private Integer listens;
    private Double revenue;

    /**
     * Default constructor
     */
    public Song() {
        this.likes = 0;
        this.listens = 0;
        this.revenue = (double) 0;
    }

    /**
     * Constructor
     * @param name
     * @param duration
     * @param album
     * @param tags
     * @param lyrics
     * @param genre
     * @param releaseYear
     * @param artist
     */
    public Song(final String name, final Integer duration, final String album,
                final ArrayList<String> tags, final String lyrics, final String genre,
                final Integer releaseYear, final String artist) {
        this.name = name;
        this.duration = duration;
        this.album = album;
        this.tags = tags;
        this.lyrics = lyrics;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.artist = artist;
        this.likes = 0;
        this.listens = 0;
        this.revenue = (double) 0;
    }

    /**
     * Equals method
     * @param o - a different Song object
     * @return
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Song song = (Song) o;
        return Objects.equals(name, song.name) && Objects.equals(duration, song.duration)
                && Objects.equals(album, song.album) && Objects.equals(tags, song.tags)
                && Objects.equals(lyrics, song.lyrics) && Objects.equals(genre, song.genre)
                && Objects.equals(releaseYear, song.releaseYear)
                && Objects.equals(artist, song.artist);
    }

    /**
     * Hash code method
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, duration, album,
                tags, lyrics, genre, releaseYear, artist);
    }

    /**
     * Increments the likes
     */
    public void addLike() {
        likes++;
    }

    /**
     * Decrements the likes
     */
    public void dislike() {
        likes--;
    }

    /**
     * Adds one more listening to the song
     */
    public void addListen() {
        listens++;
    }

    /**
     * Adds a certain revenue
     * @param revenueParam
     */
    public void addRevenue(final Double revenueParam) {
        revenue += revenueParam;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
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

    public void setReleaseYear(final Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public void setLikes(final Integer likes) {
        this.likes = likes;
    }
    public void setListens(final Integer listens) {
        this.listens = listens;
    }

    public void setRevenue(final Double revenue) {
        this.revenue = revenue;
    }
}
