package application.entities.pages;

import application.entities.library.Playlist;
import application.entities.library.Song;
import application.entities.pages.visitor.PageVisitor;
import lombok.Getter;

import java.util.ArrayList;

/**
 * This class represents a likes content page
 */
@Getter
public final class LikedContentPage implements Page {
    private ArrayList<Song> likedSongs;
    private ArrayList<Playlist> followedPlaylists;

    /**
     * Default constructor
     */
    public LikedContentPage() {
        likedSongs = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
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
     * @return - 1 for LikedContentPage
     */
    @Override
    public int whichPage() {
        return 1;
    }

    public void setLikedSongs(final ArrayList<Song> likedSongs) {
        this.likedSongs = likedSongs;
    }

    public void setFollowedPlaylists(final ArrayList<Playlist> followedPlaylists) {
        this.followedPlaylists = followedPlaylists;
    }

}
