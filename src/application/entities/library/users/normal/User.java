package application.entities.library.users.normal;

import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.Song;
import application.entities.library.users.UserDatabase;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.normal.premium.PremiumStatus;
import application.entities.pages.HomePage;
import application.entities.pages.LikedContentPage;
import application.entities.pages.Page;
import application.entities.pages.managervisitor.PageVisitor;
import application.entities.pages.typevisitor.TypePageVisitor;
import application.entities.pages.typevisitor.TypeVisitor;
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
    private boolean premium;
    private PremiumStatus premiumStatus;
    private ArrayList<String> boughtMerchandise;
    private ArrayList<String> notifications;
    private ArrayList<Page> pageHistory;
    private Integer currentPositionPH;
    private ArrayList<Song> recommendedSongs;
    private ArrayList<Playlist> recommendedPlaylists;
    // 0 - no recommendations yet
    // 1 - song ; 2 - playlist
    private Integer lastRecommendation;

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
        this.premium = false;
        this.premiumStatus =  new PremiumStatus();
        this.boughtMerchandise = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.pageHistory = new ArrayList<>();
        this.pageHistory.add(this.page);
        this.currentPositionPH = 0;
        this.recommendedPlaylists = new ArrayList<>();
        this.recommendedSongs = new ArrayList<>();
        this.lastRecommendation = 0;
    }

    /**
     * Method that puts page in acceptance for PageVisitor visitor
     * @param visitor
     */
    public void accept(final PageVisitor visitor) {
        page.accept(visitor);
    }

    /**
     * Method that puts page in acceptance for TypePageVisitor
     * @param visitor
     */
    public String accept(final TypePageVisitor visitor) {
        return page.accept(visitor);
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
        this.premium = false;
        this.premiumStatus =  new PremiumStatus();
        this.boughtMerchandise = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.pageHistory = new ArrayList<>();
        this.pageHistory.add(this.page);
        this.currentPositionPH = 0;
        this.recommendedPlaylists = new ArrayList<>();
        this.recommendedSongs = new ArrayList<>();
        this.lastRecommendation = 0;
        libraryParam.getUsers().add(this);
    }

    /**
     * Adds a new page to history
     * @param pageParam
     */
    public void addPageToHistory(final Page pageParam) {
        this.pageHistory.add(pageParam);
        this.currentPositionPH += 1;
    }

    /**
     * Go back
     */
    public void previousPage() {
        currentPositionPH--;
    }

    /**
     * This method goes to the new page after changing the
     * cursor in the history of pages
     */
    public void changePage() {
        // we get the page
        Page newPage = pageHistory.get(currentPositionPH);
        // we verify the type and put the page accordingly
        TypeVisitor visitor = new TypeVisitor();
        if (newPage.accept(visitor).equals("homePage")) {
            page = new HomePage();
        } else if (newPage.accept(visitor).equals("likedContentPage")) {
            page = new LikedContentPage();
        } else {
            page = newPage;
        }
    }

    /**
     * Go forward
     */
    public void nextPage() {
        currentPositionPH++;
    }

    /**
     * Resets the forward after a "change page command"
     */
    public void resetForward() {
        if (currentPositionPH < pageHistory.size() - 1) {
            pageHistory = new ArrayList<>(pageHistory.subList(0, currentPositionPH + 1));
        }
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

    /**
     * Takes care of managing money after cancelling a subscription
     */
    public void cancelPremiumManagement(final Library library) {
        // we check if the operation is posible
        if (!premium) {
            return;
        }
        // get the total number of songs
        int totalSongs = premiumStatus.totalNrOfSongs();
        // now we need to calculate for each artist
        for (int i = 0; i < premiumStatus.getArtists().size(); i++) {
            // we get the artist
            Artist currentArtist = premiumStatus.getArtists().get(i);
            // we calculate the songs played by this user that belongs
            // to this artist
            int totalSongsPerArtist = premiumStatus.getStates().get(i).totalNrOfSongsPerArtist();
            // we calculate the revenue
            double calculatedRevenue = (double) (1000000 * totalSongsPerArtist) / totalSongs;
            // we add the revenue
            library.addRevenueSong(calculatedRevenue, currentArtist.getUsername());
            // we add the revenue for each song
            premiumStatus.getStates().get(i).addRevenueToEachSong(calculatedRevenue);
        }
        // clear the premium status
        this.premiumStatus.getArtists().clear();
        this.premiumStatus.getStates().clear();
        // cancel the premium
        this.setPremium(false);
    }

    /**
     * Method that adds a notification
     * @param notification
     */
    public void addNotification(final String notification) {
        notifications.add(notification);
    }

    /**
     * Method that clears all notifications
     */
    public void clearNotifications() {
        notifications.clear();
    }

    /**
     * Adds a new recommended song
     * @param songParam
     */
    public void addRecommendedSong(final Song songParam) {
        recommendedSongs.add(songParam);
        lastRecommendation = 1;
    }

    /**
     * Adds a new recommended playlist
     * @param playlistParam
     */
    public void addRecommendedPlaylist(final Playlist playlistParam) {
        recommendedPlaylists.add(playlistParam);
        lastRecommendation = 2;
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

    public void setPremium(final boolean premium) {
        this.premium = premium;
    }

    public void setPremiumStatus(final PremiumStatus premiumStatus) {
        this.premiumStatus = premiumStatus;
    }

    public void setBoughtMerchandise(final ArrayList<String> boughtMerchandise) {
        this.boughtMerchandise = boughtMerchandise;
    }

    public void setNotifications(final ArrayList<String> notifications) {
        this.notifications = notifications;
    }

    public void setPageHistory(final ArrayList<Page> pageHistory) {
        this.pageHistory = pageHistory;
    }

    public void setCurrentPositionPH(final Integer currentPositionPH) {
        this.currentPositionPH = currentPositionPH;
    }

    public void setRecommendedSongs(final ArrayList<Song> recommendedSongs) {
        this.recommendedSongs = recommendedSongs;
    }

    public void setRecommendedPlaylists(final ArrayList<Playlist> recommendedPlaylists) {
        this.recommendedPlaylists = recommendedPlaylists;
    }
}
