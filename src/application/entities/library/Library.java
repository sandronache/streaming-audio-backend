package application.entities.library;

import application.entities.library.users.User;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import lombok.Getter;

import java.util.ArrayList;

import static application.constants.Constants.THREE;

/**
 * The library with the database
 */
@Getter
public final class Library {
    private ArrayList<Song> songs;
    private ArrayList<Podcast> podcasts;
    private ArrayList<User> users;
    private ArrayList<Host> hosts;
    private ArrayList<Artist> artists;

    /**
     * Constructor for the library
     * @param songs
     * @param podcasts
     * @param users
     */
    public Library(final ArrayList<Song> songs, final ArrayList<Podcast> podcasts,
                   final ArrayList<User> users) {
        this.songs = songs;
        this.podcasts = podcasts;
        this.users = users;
        this.hosts = new ArrayList<>();
        this.artists = new ArrayList<>();
    }

    /**
     * This function determines what kind of user has this username
     * @param username - the username for the user we are searching
     * @return 0 if the username doesn't exist
     *         1 if the user is a normal one
     *         2 if the user is a host
     *         3 if the user is an artist
     */
    public int typeOfUser(final String username) {
        for (User user: this.users) {
            if (user.getUsername().equals(username)) {
                return 1;
            }
        }
        for (Host host: this.hosts) {
            if (host.getUsername().equals(username)) {
                return 2;
            }
        }
        for (Artist artist: this.artists) {
            if (artist.getUsername().equals(username)) {
                return THREE;
            }
        }
        return 0;
    }

    /**
     * Gets the normal user with the given username
     * @param username
     * @return the user or null if there is not a normal user
     *          with that username
     */
    public User getUser(final String username) {
        for (User user: this.users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Gets the artist with the given username
     * @param username
     * @return the artist or null if there is not an artist
     *          with that username
     */
    public Artist getArtist(final String username) {
        for (Artist artist: artists) {
            if (artist.getUsername().equals(username)) {
                return artist;
            }
        }
        return null;
    }

    /**
     * Gets the host with the given name
     * @param username
     * @return the host or null if there is not a host
     *          with this name
     */
    public Host getHost(final String username) {
        for (Host host: hosts) {
            if (host.getUsername().equals(username)) {
                return host;
            }
        }
        return null;
    }

    /**
     * Checks if a certain song already exists
     * @param songParam
     * @return
     */
    public boolean checkIfSongExists(final Song songParam) {
        for (Song song: songs) {
            if (song.equals(songParam)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the songs from a new album if those songs
     * don't exist already
     * @param albumSongs
     */
    public void addAlbumSongs(final ArrayList<Song> albumSongs) {
        for (Song albumsong: albumSongs) {
            if (!this.checkIfSongExists(albumsong)) {
                songs.add(albumsong);
            }
        }
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setPodcasts(final ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    public void setUsers(final ArrayList<User> users) {
        this.users = users;
    }

    public void setHosts(final ArrayList<Host> hosts) {
        this.hosts = hosts;
    }

    public void setArtists(final ArrayList<Artist> artists) {
        this.artists = artists;
    }
}
