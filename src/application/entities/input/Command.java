package application.entities.input;

import application.entities.library.Episode;
import application.entities.library.Song;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Command stores the fields given as input for each command
 */
@Getter
public final class Command {
    private String command;
    private String username;
    private Integer timestamp;
    private String type;
    private Filter filters;
    private Integer itemNumber;
    private String playlistName;
    private Integer playlistId;
    private Integer seed;
    private String nextPage;
    private Integer age;
    private String city;
    private String name;
    private Integer releaseYear;
    private ArrayList<Song> songs;
    private String date;
    private Integer price;
    private ArrayList<Episode> episodes;
    private String description;
    private String recommendationType;

    public void setCommand(final String command) {
        this.command = command;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setTimestamp(final Integer timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setItemNumber(final Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

    public void setFilters(final Filter filters) {
        this.filters = filters;
    }

    public void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    public void setPlaylistId(final Integer playlistId) {
        this.playlistId = playlistId;
    }

    public void setSeed(final Integer seed) {
        this.seed = seed;
    }

    public void setNextPage(final String nextPage) {
        this.nextPage = nextPage;
    }

    public void setAge(final Integer age) {
        this.age = age;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setReleaseYear(final Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public void setPrice(final Integer price) {
        this.price = price;
    }

    public void setEpisodes(final ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setRecommendationType(final String recommendationType) {
        this.recommendationType = recommendationType;
    }
}
