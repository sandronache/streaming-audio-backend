package application.entities.library;

import application.entities.library.users.AccountArtist;
import application.entities.library.users.normal.User;
import application.entities.library.users.artist.Album;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import application.entities.library.users.normal.premium.ArtistPremiumStatus;
import application.entities.library.users.normal.premium.PremiumStatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;

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
    private ArrayList<AccountArtist> accountsAllArtists;

    /**
     * Constructor for the library
     * @param songs
     * @param podcasts
     * @param users
     */
    public Library(final ArrayList<Song> songs, final ArrayList<Podcast> podcasts,
                   final ArrayList<User> users, final ArrayList<AccountArtist> accounts) {
        this.songs = songs;
        this.podcasts = podcasts;
        this.users = users;
        this.hosts = new ArrayList<>();
        this.artists = new ArrayList<>();
        this.accountsAllArtists = accounts;
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

    /**
     * This method does all the work of adding all details necessary after
     * playling a song in a player
     * @param usernameParam
     * @param songParam
     */
    public void addSongForUser(final String usernameParam, final Song songParam) {
        // we find the normal user if possible
        User currentUser = this.getUser(usernameParam);
        if (currentUser == null) {
            return;
        }
        // we add the song and the genre
        currentUser.getWrapped().addSong(songParam);
        currentUser.getWrapped().addGenre(songParam.getGenre());
        // we check if the artist has a page, so we can add him too
        Artist currentArtist = this.getArtist(songParam.getArtist());
        if (currentArtist == null) {
            return;
        }
        // we add the artist
        currentUser.getWrapped().addArtist(currentArtist);
        // now we want to find the album where this song is:
        for (Album currentArtistAlbum: currentArtist.getAlbums()) {
            if (currentArtistAlbum.getName().equals(songParam.getAlbum())) {
                currentUser.getWrapped().addAlbum(currentArtistAlbum);
                currentArtistAlbum.addListen();
                break;
            }
        }
        // we add a new like to the song
        songParam.addListen();
        // we also change the status of this artist as "played"
        this.artistGotPlayed(songParam.getArtist());
        // if the user is a premium subscriber we need to track his activity
        if (currentUser.isPremium()) {
            PremiumStatus tempStatus = currentUser.getPremiumStatus();
            // we try to see if we already have the artist
            for (int i = 0; i < tempStatus.getArtists().size(); i++) {
                Artist userArtist = tempStatus.getArtists().get(i);
                // if we do we try to see if we already have his song
                if (userArtist.getUsername().equals(songParam.getArtist())) {
                    // if we do we try to see if we already have the song
                    for (int j = 0; j < tempStatus.getStates().get(i).getSongs().size(); j++) {
                        Song songArtist = tempStatus.getStates().get(i).getSongs().get(j);
                        // if we do we add 1
                        if (songArtist.getName().equals(songParam.getName())) {
                            Integer value = tempStatus.getStates().get(i).getTimes().get(j);
                            tempStatus.getStates().get(i).getTimes().set(j, value + 1);
                            return;
                        }
                    }
                    // if the song doesn't exist we add the song
                    tempStatus.getStates().get(i).addNewPair(songParam);
                    return;
                }
            }
            // if not we need to add the artist and the song
            tempStatus.getArtists().add(currentArtist);
            tempStatus.getStates().add(new ArtistPremiumStatus(songParam));
        }
    }

    /**
     * This method does all the work of adding all details necessary after
     * playling an episode in a player
     * @param usernameUser
     * @param episodeParam
     * @param usernameHost
     */
    public void addEpisodeForUserAndHost(final String usernameUser,
                                         final Episode episodeParam,
                                         final String usernameHost) {
        // we find the normal user if possible
        User currentUser = this.getUser(usernameUser);
        if (currentUser == null) {
            return;
        }
        // we add the episode
        currentUser.getWrapped().addEpisode(episodeParam);
        // we add a like to the episode
        episodeParam.addListen();
        // we find the host of the podcast if possible
        Host currentHost = this.getHost(usernameHost);
        if (currentHost == null) {
            return;
        }
        // we add the new listener to the host
        currentHost.addListener(currentUser);
    }

    /**
     * This method creates a new account if necessary
     * @param userArtist
     */
    public void checkIfExistsAccount(final String userArtist) {
        for (AccountArtist iter: this.accountsAllArtists) {
            if (userArtist.equals(iter.getUsername())) {
                return;
            }
        }
        this.accountsAllArtists.add(new AccountArtist(userArtist));
    }

    /**
     * This method searhes a certain artist and changes the status of "played"
     * @param userArtist
     */
    public void artistGotPlayed(final String userArtist) {
        for (AccountArtist iter: this.accountsAllArtists) {
            if (userArtist.equals(iter.getUsername())) {
                iter.gotPlayed();
            }
        }
    }

    /**
     * This method is for sorting the accounts
     */
    public void sortAccounts() {
        // remove the account for artists which were not even played
        accountsAllArtists.removeIf(account -> !account.isPlayedOrNot());
        // sorting
        accountsAllArtists.sort(
                Comparator.comparing(AccountArtist::getTotalRevenue).reversed()
                        .thenComparing(AccountArtist::getUsername)
        );
    }

    /**
     * Method that adds revenue corresponding to the artist given as parameter
     * @param revenue
     * @param usernameParam
     */
    public void addRevenue(final Double revenue, final String usernameParam) {
        for (AccountArtist accountsAllArtist : accountsAllArtists) {
            if (accountsAllArtist.getUsername().equals(usernameParam)) {
                accountsAllArtist.addRevenueSong(revenue);
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

    public void setAccountsAllArtists(final ArrayList<AccountArtist> accountsAllArtists) {
        this.accountsAllArtists = accountsAllArtists;
    }
}
