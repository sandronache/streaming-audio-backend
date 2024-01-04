package application.entities.library.users.normal;

import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.Song;
import application.entities.library.users.UserDatabase;
import application.entities.pages.HomePage;
import application.entities.pages.Page;
import application.entities.pages.visitor.PageVisitor;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public final class User implements UserDatabase {
    private String username;
    private int age;
    private String city;
    // true - online ; false - offline
    private boolean status;
    private ArrayList<Playlist> playlists;
    private ArrayList<Song> likedSongs;
    private ArrayList<Playlist> followedPlaylists;
    private Page page;
    private ArrayList<String> lastSearch;
    private ArrayList<String> artistLastSearch;
    private ArrayList<Song> songsLastSearched;
    private String typeLastSearch;
    private String lastSelected;
    private String asideLastSelected;
    private String artistLastSelected;
    private Song songLastSelected;
    private boolean searched;
    private WrappedUser wrapped;

    /**
     * Default constructor
     */
    public User() { }

    /**
     * Constructor
     * @param username
     * @param age
     * @param city
     */
    public User(final String username, final int age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.status = true;
        this.playlists = new ArrayList<>();
        this.likedSongs = new ArrayList<>();
        this.page = new HomePage();
        this.lastSearch = new ArrayList<>();
        this.artistLastSearch = new ArrayList<>();
        this.songsLastSearched = new ArrayList<>();
        this.lastSelected = null;
        this.asideLastSelected = null;
        this.artistLastSelected = null;
        this.songLastSelected = null;
        this.typeLastSearch = null;
        this.followedPlaylists = new ArrayList<>();
        this.searched = false;
        this.wrapped = new WrappedUser();
    }

    /**
     * Method that puts page in acceptance for a visitor
     * @param visitor
     */
    public void accept(final PageVisitor visitor) {
        page.accept(visitor);
    }

    /**
     * Function that builds an object(Artist)
     * @param usernameParam
     * @param ageParam
     * @param cityParam
     */
    @Override
    public void build(final String usernameParam, final int ageParam,
                      final String cityParam, final Library libraryParam) {
        this.username = usernameParam;
        this.age = ageParam;
        this.city = cityParam;
        this.status = true;
        this.playlists = new ArrayList<>();
        this.likedSongs = new ArrayList<>();
        this.followedPlaylists = new ArrayList<>();
        this.page = new HomePage();
        this.lastSearch = new ArrayList<>();
        this.artistLastSearch = new ArrayList<>();
        this.songsLastSearched = new ArrayList<>();
        this.lastSelected = null;
        this.artistLastSelected = null;
        this.songLastSelected = null;
        this.typeLastSearch = null;
        this.searched = false;
        this.wrapped = new WrappedUser();
        libraryParam.getUsers().add(this);
    }

    /**
     * Switches the status
     */
    public void switchStatus() {
        this.status = !this.status;
    }

    /**
     * Adds new playlist
     * @param playlist
     */
    public void addPlaylist(final Playlist playlist) {
        this.playlists.add(playlist);
    }

    /**
     * Adds new liked song
     * @param song
     */
    public void addLikedSong(final Song song) {
        this.likedSongs.add(song);
    }

    /**
     * Adds new followed playlist
     * @param playlist
     */
    public void addLikedPlaylist(final Playlist playlist) {
        this.followedPlaylists.add(playlist);
    }

    /**
     * Removes a playlist from the followed playlists
     * @param playlist
     */
    public void removeLikedPlaylist(final Playlist playlist) {
        this.followedPlaylists.remove(playlist);
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public void setStatus(final boolean status) {
        this.status = status;
    }

    public void setPlaylists(final ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public void setLikedSongs(final ArrayList<Song> likedSongs) {
        this.likedSongs = likedSongs;
    }

    public void setFollowedPlaylists(final ArrayList<Playlist> followedPlaylists) {
        this.followedPlaylists = followedPlaylists;
    }

    public void setPage(final Page page) {
        this.page = page;
    }

    public void setLastSearch(final ArrayList<String> lastSearch) {
        this.lastSearch = lastSearch;
    }

    public void setArtistLastSearch(final ArrayList<String> artistLastSearch) {
        this.artistLastSearch = artistLastSearch;
    }

    public void setSongsLastSearched(final ArrayList<Song> songsLastSearched) {
        this.songsLastSearched = songsLastSearched;
    }

    public void setTypeLastSearch(final String typeLastSearch) {
        this.typeLastSearch = typeLastSearch;
    }

    public void setLastSelected(final String lastSelected) {
        this.lastSelected = lastSelected;
    }

    public void setArtistLastSelected(final String artistLastSelected) {
        this.artistLastSelected = artistLastSelected;
    }

    public void setSongLastSelected(final Song songLastSelected) {
        this.songLastSelected = songLastSelected;
    }

    public void setSearched(final boolean searched) {
        this.searched = searched;
    }

    public void setAsideLastSelected(final String asideLastSelected) {
        this.asideLastSelected = asideLastSelected;
    }

    public void setWrapped(final WrappedUser wrapped) {
        this.wrapped = wrapped;
    }
}
