package application.commands.admin.recommendations;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.Song;
import application.entities.library.users.normal.User;
import application.entities.library.users.artist.Artist;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Random;

import static application.constants.Constants.FIVE;
import static application.constants.Constants.THREE;

/**
 * Class for update recommendations command
 */
@Getter
public final class UpdateRecommendations extends PlayerRelatedCommands {
    private String command;
    private String username;
    private Integer timestamp;
    private String recommendationType;
    private Library library;
    private ArrayList<Player> players;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param library
     */
    public UpdateRecommendations(final String command, final String username,
                    final Integer timestamp, final String recommendationType,
                     final Library library, final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.recommendationType = recommendationType;
        this.library = library;
        this.players = players;
    }

    /**
     * Method that determines the new recommended song
     * @param currentPlayer
     * @return
     */
    public Song getSongRecommendation(final Player currentPlayer) {
        // we calculate the seed
        int seed = currentPlayer.getSong().getDuration()
                - currentPlayer.getRemainedTime();
        // we make an array with all songs from the same genre
        ArrayList<Song> sameGenreSongs =  new ArrayList<>();
        for (Song songLibrary: library.getSongs()) {
            if (songLibrary.getGenre().equals(currentPlayer.getSong().getGenre())) {
                sameGenreSongs.add(songLibrary);
            }
        }
        // we get the random song
        Random random = new Random(seed);
        return sameGenreSongs.get(random.nextInt(sameGenreSongs.size()));
    }

    /**
     * Method that increases the appearance of a genre or adds
     * the new genre
     * @param genresList
     * @param scoreGenre
     * @param genreParam
     */
    private void checkIfGenreExists(final ArrayList<String> genresList,
                                    final ArrayList<Integer> scoreGenre,
                                    final String genreParam) {
        for (int i = 0; i < genresList.size(); i++) {
            if (genresList.get(i).equals(genreParam)) {
                scoreGenre.set(i, scoreGenre.get(i) + 1);
                return;
            }
        }
        genresList.add(genreParam);
        scoreGenre.add(1);
    }

    /**
     * Method to get the max genre locally
     * @param genresList
     * @param scoreGenre
     * @return
     */
    private String getLocalMaxGenre(final ArrayList<String> genresList,
                                    final ArrayList<Integer> scoreGenre) {
        String genreResult;
        int max = 0;
        int index = -1;
        for (int i = 0; i < genresList.size(); i++) {
            if (scoreGenre.get(i) > max) {
                max = scoreGenre.get(i);
                index = i;
            }
        }
        if (index == -1) {
            return null;
        }
        genreResult = genresList.get(index);
        genresList.remove(index);
        scoreGenre.remove(index);
        return genreResult;
    }

    /**
     * This method adds a given number of songs from the specific genre to the list
     * given as param
     * @param genreParam - genre
     * @param songsForPlaylist - list
     * @param countParam - times to add
     */
    private void addNeededSongs(final String genreParam,
                                final ArrayList<Song> songsForPlaylist,
                                final Integer countParam) {
        int count = countParam;
        while (true) {
            // for likes songs
            for (Song likedSong: library.getUser(username).getLikedSongs()) {
                if (likedSong.getGenre().equals(genreParam)) {
                    songsForPlaylist.add(likedSong);
                    count--;
                    if (count == 0) {
                        break;
                    }
                }
            }
            if (count == 0) {
                break;
            }
            // for own Playlists
            for (Playlist ownPlaylist: library.getUser(username).getPlaylists()) {
                for (Song songPlaylist: ownPlaylist.getSongs()) {
                    if (songPlaylist.getGenre().equals(genreParam)) {
                        songsForPlaylist.add(songPlaylist);
                        count--;
                        if (count == 0) {
                            break;
                        }
                    }
                }
            }
            if (count == 0) {
                break;
            }
            // for followed Playlists
            for (Playlist ownPlaylist: library.getUser(username)
                    .getFollowedPlaylists()) {
                for (Song songPlaylist: ownPlaylist.getSongs()) {
                    if (songPlaylist.getGenre().equals(genreParam)) {
                        songsForPlaylist.add(songPlaylist);
                        count--;
                        if (count == 0) {
                            break;
                        }
                    }
                }
            }
            break;
        }
    }

    /**
     * Method that creates a new playlist based on users base
     * @return
     */
    public Playlist getPlaylistRecommendation() {
        // the songs for the playlist
        ArrayList<Song> songsForPlaylist = new ArrayList<>();
        // list for genres
        ArrayList<String> genresList = new ArrayList<>();
        // list for total for all genres
        ArrayList<Integer> scoreGenre = new ArrayList<>();
        // for likes songs
        for (Song likedSong: library.getUser(username).getLikedSongs()) {
            this.checkIfGenreExists(genresList,
                    scoreGenre, likedSong.getGenre());
        }
        // for own Playlists
        for (Playlist ownPlaylist: library.getUser(username).getPlaylists()) {
            for (Song songPlaylist: ownPlaylist.getSongs()) {
                this.checkIfGenreExists(genresList,
                        scoreGenre, songPlaylist.getGenre());
            }
        }
        // for followed Playlists
        for (Playlist ownPlaylist: library.getUser(username)
                .getFollowedPlaylists()) {
            for (Song songPlaylist: ownPlaylist.getSongs()) {
                this.checkIfGenreExists(genresList,
                        scoreGenre, songPlaylist.getGenre());
            }
        }
        // we get the top genres
        String genre1 = this.getLocalMaxGenre(genresList, scoreGenre);
        String genre2 = this.getLocalMaxGenre(genresList, scoreGenre);
        String genre3 = this.getLocalMaxGenre(genresList, scoreGenre);
        // get the songs from each genre
        this.addNeededSongs(genre1, songsForPlaylist, FIVE);
        this.addNeededSongs(genre2, songsForPlaylist, THREE);
        this.addNeededSongs(genre3, songsForPlaylist, 2);
        return new Playlist(false, username
                + "'s recommendations", 0, 0, 0);
    }

    /**
     * Method that creates a new playlist based fans
     * @return
     */
    public Playlist getFansRecommendation(final Player currentPlayer) {
        // the songs for the playlist
        ArrayList<Song> songsForPlaylist = new ArrayList<>();
        // we get the artist on the player
        Artist artistPlayer = library.getArtist(currentPlayer.getSong().getArtist());
        for (int i = 0; i < artistPlayer.getSubscribers().size()
            && i < FIVE; i++) {
            User fan = artistPlayer.getSubscribers().get(i);
            for (int j = 0; j < fan.getLikedSongs().size()
                && j < FIVE; j++) {
                Song songFan = fan.getLikedSongs().get(j);
                songsForPlaylist.add(songFan);
            }
        }
        return new Playlist(false,
                currentPlayer.getSong().getArtist() + " Fan Club recommendations",
                0, 0, 0);
    }
    @Override
    public void startCommand(final ObjectMapper objectMapper, final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        // we check if the username exists
        if (library.getUser(username) == null) {
            node.put("message", "The username " + username + " doesn't exists.");
            outputs.add(node);
            return;
        } else if (library.typeOfUser(username) != 1) {
            node.put("message", username + " is not a normal user.");
            outputs.add(node);
            return;
        }
        // we get the user
        User user =  library.getUser(username);
        // we first update the player
        Player currentPlayer = this.getCurrentPlayer(players, this.getUsername());
        if (currentPlayer == null || currentPlayer.getName().isEmpty()
                || currentPlayer.isPaused()) {
            node.put("message",
                    "Please load a source before wanting to go to an site");
            outputs.add(node);
            return;
        }
        this.updatePlayer(currentPlayer, this.timestamp);
        currentPlayer.setTimestamp(timestamp);
        // start cases
        if (this.recommendationType.equals("random_song")) {
            if (currentPlayer.getSong().getDuration()
                    - currentPlayer.getRemainedTime() >= 30) {
                user.addRecommendedSong(this.getSongRecommendation(currentPlayer));
            } else {
                node.put("message",
                        "No new recommendations were found.");
                outputs.add(node);
                return;
            }
        } else if (this.recommendationType.equals("random_playlist")) {
            Playlist tempPlaylist = this.getPlaylistRecommendation();
            user.addRecommendedPlaylist(tempPlaylist);
        } else {
            Playlist tempPlaylist = this.getFansRecommendation(currentPlayer);
            user.addRecommendedPlaylist(tempPlaylist);
        }

        node.put("message", "The recommendations for user "
                + username + " have been updated successfully.");
        outputs.add(node);
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setTimestamp(final Integer timestamp) {
        this.timestamp = timestamp;
    }

    public void setRecommendationType(final String recommendationType) {
        this.recommendationType = recommendationType;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public void setPlayers(final ArrayList<Player> players) {
        this.players = players;
    }

}
