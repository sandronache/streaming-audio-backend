package application.entities.library;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class for a playlist
 */
@Getter
public final class Playlist {
    // true = public | false = private
    private boolean visibility;
    private String name;
    private ArrayList<Song> songs;
    private Integer id;
    private Integer globalPlaylistId;
    private Integer followers;
    private ArrayList<String> theFollowers;

    public Playlist() { }

    /**
     * Constructor
     * @param visibility
     * @param name
     * @param id
     * @param followers
     * @param globalPlaylistId for keeeping track of the history for all playlists
     */
    public Playlist(final boolean visibility, final String name, final Integer id,
                    final Integer followers, final Integer globalPlaylistId) {
        this.visibility = visibility;
        this.name = name;
        this.songs = new ArrayList<>();
        this.id = id;
        this.globalPlaylistId = globalPlaylistId;
        this.followers = followers;
        this.theFollowers = new ArrayList<>();
    }

    /**
     * Adds new follower
     * @param username
     */
    public void addFollower(final String username) {
        this.theFollowers.add(username);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Playlist playlist = (Playlist) o;
        return visibility == playlist.visibility
                && Objects.equals(name, playlist.name)
                && Objects.equals(songs, playlist.songs)
                && Objects.equals(id, playlist.id)
                && Objects.equals(globalPlaylistId, playlist.globalPlaylistId)
                && Objects.equals(followers, playlist.followers)
                && Objects.equals(theFollowers, playlist.theFollowers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visibility, name, songs, id, globalPlaylistId, followers, theFollowers);
    }

    public void setVisibility(final boolean visibility) {
        this.visibility = visibility;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public void setFollowers(final Integer followers) {
        this.followers = followers;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setGlobalPlaylistId(final Integer globalPlaylistId) {
        this.globalPlaylistId = globalPlaylistId;
    }
}
