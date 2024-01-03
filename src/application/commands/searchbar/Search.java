package application.commands.searchbar;

import application.commands.root.PlayerRelatedCommands;
import application.entities.input.Filter;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.Podcast;
import application.entities.library.Song;
import application.entities.library.users.User;
import application.entities.library.users.artist.Album;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

import static application.constants.Constants.FIVE;
import static application.constants.Constants.FOUR;

/**
 * Class for Search command
 */
@Getter
public final class Search extends PlayerRelatedCommands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private String type;
    private Filter filters;
    private ArrayList<Player> players;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param type
     * @param filters
     * @param library
     * @param players
     */
    public Search(final String command, final String username, final Integer timestamp,
                  final String type, final  Filter filters, final Library library,
                  final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.type = type;
        this.filters = filters;
        this.players = players;
    }

    /**
     * Finds the player for this user and removes if something is loaded
     */
    public void resetPlayer() {
        Player currentPlayer = this.getCurrentPlayer(players, this.getUsername());
        if (currentPlayer == null || currentPlayer.getName().isEmpty()) {
            return;
        }
        switch (currentPlayer.getType()) {
            case "song" -> currentPlayer.setNewStatusSong(currentPlayer.getSong(),
                    "", "song",
                    0, timestamp, 0, false, true);
            case "podcast" -> {
                currentPlayer.checkIfEndedPodcast(this.timestamp);
                currentPlayer.setNewStatusPodcast("", "",
                        "podcast", timestamp,
                        0, false, true);
            }
            case "playlist" -> currentPlayer.setNewStatusPlaylist(currentPlayer.getSong(),
                    "", "playlist",
                    currentPlayer.getPlaylist(), 0, timestamp,
                    0, false, true);
            case "album" -> currentPlayer.setNewStatusAlbum(currentPlayer.getSong(),
                    "", "album",
                    currentPlayer.getAlbum(), 0, timestamp,
                    0, false, true);
            default -> { }
        }
    }

    /**
     * Search starting point
     * @return
     */
    public void searchStart(final ArrayList<String> lastSearch,
                            final ArrayList<String> artistLastSearch,
                            final ArrayList<Song> songsLastSearched) {
        switch (this.type) {
            case "song" -> this.searchSong(lastSearch, songsLastSearched);
            case "podcast" -> this.searchPodcast(lastSearch, artistLastSearch);
            case "playlist" -> this.searchPlaylist(lastSearch, artistLastSearch);
            case "album" -> this.searchAlbum(lastSearch, artistLastSearch);
            case "artist" -> this.searchArtist(lastSearch);
            default -> this.searchHost(lastSearch);
        }
    }

    /**
     * Search for song
     * @return
     */
    public void searchSong(final ArrayList<String> lastSearch,
                           final ArrayList<Song> songsLastSearched) {
        for (Song song : library.getSongs()) {
            if (this.filters.getName() != null) {
                if (!song.getName().startsWith(this.filters.getName())) {
                    continue;
                }
            }
            if (this.filters.getAlbum() != null) {
                if (!song.getAlbum().equals(this.filters.getAlbum())) {
                    continue;
                }
            }
            if (this.filters.getLyrics() != null) {
                String tmp1 = song.getLyrics().toLowerCase();
                String tmp2 = this.filters.getLyrics().toLowerCase();
                if (!tmp1.contains(tmp2)) {
                    continue;
                }
            }
            if (this.filters.getGenre() != null) {
                if (!song.getGenre().equalsIgnoreCase(this.filters.getGenre())) {
                    continue;
                }
            }
            if (this.filters.getArtist() != null) {
                if (!song.getArtist().equals(this.filters.getArtist())) {
                    continue;
                }
            }
            if (this.filters.getReleaseYear() != null) {
                String yearString = this.filters.getReleaseYear().
                        substring(this.filters.getReleaseYear().length() - FOUR);
                int year = Integer.parseInt(yearString);
                if (this.filters.getReleaseYear().startsWith("<")) {
                    if (song.getReleaseYear() >= year) {
                        continue;
                    }
                } else if (this.filters.getReleaseYear().startsWith(">")) {
                    if (song.getReleaseYear() <= year) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            if (this.filters.getTags() != null) {
                int result1 = 0;
                for (String ourTag : this.filters.getTags()) {
                    result1 = 0;
                    for (String tag : song.getTags()) {
                        if (ourTag.equals(tag)) {
                            result1 = 1;
                            break;
                        }
                    }
                    if (result1 == 0) {
                        break;
                    }
                }
                if (result1 == 0) {
                    continue;
                }
            }
            lastSearch.add(song.getName());
            songsLastSearched.add(song);
            if (lastSearch.size() == FIVE) {
                break;
            }
        }
    }

    /**
     * Search for Podcast
     * @return
     */
    public void searchPodcast(final ArrayList<String> lastSearch,
                              final ArrayList<String> artistLastSearch) {
        for (Podcast podcast : library.getPodcasts()) {
            if (this.filters.getName() != null) {
                if (!podcast.getName().startsWith(this.filters.getName())) {
                    continue;
                }
            }
            if (this.filters.getOwner() != null) {
                if (!podcast.getOwner().equals(this.filters.getOwner())) {
                    continue;
                }
            }
            lastSearch.add(podcast.getName());
            artistLastSearch.add(podcast.getOwner());
            if (lastSearch.size() == FIVE) {
                break;
            }
        }
    }

    /**
     * Search for playlist
     * @return
     */
    public void searchPlaylist(final ArrayList<String> lastSearch,
                               final ArrayList<String> artistLastSearch) {
        for (User user: library.getUsers()) {
            for (Playlist playlist : user.getPlaylists()) {
                int result = 0;
                if (this.filters.getOwner() != null && (playlist.isVisibility()
                        || user.getUsername().equals(this.getUsername()))) {
                    if (user.getUsername().equals(this.filters.getOwner())) {
                        result = 1;
                    } else {
                        continue;
                    }
                }
                if (this.filters.getName() != null) {
                    if (playlist.getName().startsWith(this.filters.getName())
                            && (playlist.isVisibility()
                            || user.getUsername().equals(this.getUsername()))) {
                        result = 1;
                    } else {
                        continue;
                    }
                }
                if (result == 1) {
                    lastSearch.add(playlist.getName());
                    artistLastSearch.add(user.getUsername());
                    if (lastSearch.size() == FIVE) {
                        break;
                    }
                }
            }
            if (lastSearch.size() == FIVE) {
                break;
            }
        }
    }

    /**
     * Search for album
     * @return
     */
    public void searchAlbum(final ArrayList<String> lastSearch,
                            final ArrayList<String> artistLastSearch) {
        for (Artist artist: library.getArtists()) {
            for (Album album : artist.getAlbums()) {
                int result = 0;
                if (this.filters.getOwner() != null) {
                    if (artist.getUsername().startsWith(this.filters.getOwner())) {
                        result = 1;
                    } else {
                        continue;
                    }
                }
                if (this.filters.getName() != null) {
                    if (album.getName().startsWith(this.filters.getName())) {
                        result = 1;
                    } else {
                        continue;
                    }
                }
                if (this.filters.getDescription() != null) {
                    if (album.getDescription().startsWith(
                            this.filters.getDescription())) {
                        result = 1;
                    } else {
                        continue;
                    }
                }
                if (result == 1) {
                    lastSearch.add(album.getName());
                    artistLastSearch.add(artist.getUsername());
                    if (lastSearch.size() == FIVE) {
                        break;
                    }
                }
            }
            if (lastSearch.size() == FIVE) {
                break;
            }
        }
    }

    /**
     * Search for an artist
     */
    public void searchArtist(final ArrayList<String> lastSearch) {
        for (Artist artist: library.getArtists()) {
            if (this.filters.getName() != null) {
                if (artist.getUsername().startsWith(this.filters.getName())) {
                    lastSearch.add(artist.getUsername());
                    if (lastSearch.size() == FIVE) {
                        break;
                    }
                }
            }
        }
    }
    /**
     * Search for a host
     */
    public void searchHost(final ArrayList<String> lastSearch) {
        for (Host host: library.getHosts()) {
            if (this.filters.getName() != null) {
                if (host.getUsername().startsWith(this.filters.getName())) {
                    lastSearch.add(host.getUsername());
                    if (lastSearch.size() == FIVE) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Method that applies the command and prints
     * @param objectMapper
     * @param outputs
     */
    public void startCommand(final ObjectMapper objectMapper, final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        // if offline user can't do anything
        if (!library.getUser(username).isStatus()) {
            node.put("message", username + " is offline.");
            ArrayNode arrayNode = objectMapper.createArrayNode();
            node.set("results", arrayNode);
            outputs.add(node);
            return;
        }

        // we get the user
        User user = library.getUser(username);
        // reset
        user.getLastSearch().clear();
        user.getSongsLastSearched().clear();
        user.getArtistLastSearch().clear();

        // reset player and do the searching
        this.resetPlayer();
        this.searchStart(user.getLastSearch(),
                user.getArtistLastSearch(), user.getSongsLastSearched());

        // print the result
        String str = "Search returned " + user.getLastSearch().size() + " results";
        node.put("message", str);
        ArrayNode arrayNode;
        if (!user.getLastSearch().isEmpty()) {
            arrayNode = objectMapper.valueToTree(user.getLastSearch());
            user.setTypeLastSearch(type);
        } else {
            arrayNode = objectMapper.createArrayNode();
        }
        node.set("results", arrayNode);
        user.setSearched(true);
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

    public void setType(final String type) {
        this.type = type;
    }

    public void setFilters(final Filter filters) {
        this.filters = filters;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public void setPlayers(final ArrayList<Player> players) {
        this.players = players;
    }
}
