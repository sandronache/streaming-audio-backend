package application.entities.pages;

import application.entities.library.Playlist;
import application.entities.library.Song;
import application.entities.pages.managervisitor.PageVisitor;
import application.entities.pages.typevisitor.TypePageVisitor;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for home page
 */
@Getter
public final class HomePage implements Page {
    private ArrayList<Song> songs;
    private ArrayList<Playlist> playlists;
    private ArrayList<Song> recommendedSongs;
    private ArrayList<Playlist> recommendedPlaylists;

    /**
     * Default constructor
     */
    public HomePage() {
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        recommendedSongs = new ArrayList<>();
        recommendedPlaylists = new ArrayList<>();
    }

    /**
     * Method that accepts the visitor PageVisitor
     * @param visitor
     */
    @Override
    public void accept(final PageVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Method that accepts the visitor TypePageVisitor
     * @param visitor
     */
    @Override
    public String accept(final TypePageVisitor visitor) {
        return visitor.visit(this);
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setPlaylists(final ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public void setRecommendedSongs(final ArrayList<Song> recommendedSongs) {
        this.recommendedSongs = recommendedSongs;
    }

    public void setRecommendedPlaylists(final ArrayList<Playlist> recommendedPlaylists) {
        this.recommendedPlaylists = recommendedPlaylists;
    }
}
