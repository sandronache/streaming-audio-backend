package application.entities.pages;

import application.entities.library.Playlist;
import application.entities.library.Song;
import application.entities.pages.visitor.PageVisitor;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for home page
 */
@Getter
public final class HomePage implements Page {
    private ArrayList<Song> songs;
    private ArrayList<Playlist> playlists;

    /**
     * Default constructor
     */
    public HomePage() {
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
    }

    /**
     * Method that accepts the visitor
     * @param visitor
     */
    @Override
    public void accept(final PageVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Implementation of whichPage method
     * @return - 0 for HomePage
     */
    @Override
    public int whichPage() {
        return 0;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setPlaylists(final ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

}
