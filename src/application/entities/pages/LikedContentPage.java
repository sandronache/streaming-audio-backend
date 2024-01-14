package application.entities.pages;

import application.entities.library.Playlist;
import application.entities.library.Song;
import application.entities.pages.managervisitor.PageVisitor;
import application.entities.pages.typevisitor.TypePageVisitor;
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

    public void setLikedSongs(final ArrayList<Song> likedSongs) {
        this.likedSongs = likedSongs;
    }

    public void setFollowedPlaylists(final ArrayList<Playlist> followedPlaylists) {
        this.followedPlaylists = followedPlaylists;
    }

}
