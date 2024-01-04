package application.commands.player;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.Podcast;
import application.entities.library.Song;
import application.entities.library.users.normal.User;
import application.entities.library.users.artist.Album;
import application.entities.library.users.artist.Artist;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for Load command
 */
@Getter
public final class Load extends PlayerRelatedCommands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private ArrayList<Player> players;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param library
     * @param library
     * @param players
     */
    public Load(final String command, final String username, final Integer timestamp,
                 final Library library, final ArrayList<Player> players) {
        this.command = command;
        this.timestamp = timestamp;
        this.username = username;
        this.library = library;
        this.players = players;
    }

    /**
     * Method applies command and prints
     * @param objectMapper
     * @param outputs
     */
    public void startCommand(final ObjectMapper objectMapper, final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        if (!library.getUser(username).isStatus()) {
            node.put("message", username + " is offline.");
            outputs.add(node);
            return;
        }

        User user = library.getUser(username);

        if (user.getLastSelected() == null && user.getAsideLastSelected() == null) {
            node.put("message",
                    "Please select a source before attempting to load.");
            outputs.add(node);
            return;
        }

        if (user.getLastSelected() == null && user.getAsideLastSelected() != null) {
            user.setLastSelected(user.getAsideLastSelected());
            user.setAsideLastSelected(null);
        }

        Player currentPlayer = this.getCurrentPlayer(players, this.getUsername());
        if (currentPlayer == null) {
            currentPlayer = new Player(this.getUsername(), library);
            players.add(currentPlayer);
        } else if (!currentPlayer.isPaused() && !currentPlayer.getName().isEmpty()) {
            this.updatePlayer(currentPlayer, this.timestamp);
        }

        switch (user.getTypeLastSearch()) {
            case "song" -> {
                for (Song otherSong : library.getSongs()) {
                    if (otherSong.equals(user.getSongLastSelected())) {
                        currentPlayer.setNewStatusSong(user.getSongLastSelected(),
                                user.getSongLastSelected().getName(), "song",
                                user.getSongLastSelected().getDuration(), this.timestamp,
                                0, false, false);
                        // we add the new data for the song in the wrapped
                        library.addSongForUser(this.username, user.getSongLastSelected());
                    }
                }
            }
            case "podcast" -> {
                for (Podcast podcast : library.getPodcasts()) {
                    if (user.getLastSelected().equals(podcast.getName())
                            && user.getArtistLastSelected().equals(podcast.getOwner())) {
                        currentPlayer.setNewStatusPodcast(user.getLastSelected(),
                                user.getArtistLastSelected(), "podcast", this.timestamp,
                                0, false, false);
                        // we add all the details necessary for playing this podcast to the wrapped
                        library.addEpisodeForUserAndHost(this.username,
                                podcast.getEpisodes().get(0), podcast.getOwner());
                    }
                }
            }
            case "playlist" -> {
                for (User user1 : library.getUsers()) {
                    for (Playlist playlist : user1.getPlaylists()) {
                        if (playlist.getName().equals(user.getLastSelected())) {
                            if (playlist.getSongs().isEmpty()) {
                                node.put("message",
                                        "You can't load an empty audio collection!");
                                outputs.add(node);
                                return;
                            }
                            currentPlayer.setNewStatusPlaylist(playlist.getSongs().get(0),
                                    user.getLastSelected(), "playlist", playlist,
                                    playlist.getSongs().get(0).getDuration(),
                                    this.timestamp, 0, false, false);
                            // we add the new data for the song in the wrapped
                            library.addSongForUser(this.username, playlist.getSongs().get(0));
                        }
                    }
                }
            }
            case "album" -> {
                for (Artist artist : library.getArtists()) {
                    for (Album album : artist.getAlbums()) {
                        if (album.getName().equals(user.getLastSelected())
                            && artist.getUsername().equals(user.getArtistLastSelected())) {
                            if (album.getSongs().isEmpty()) {
                                node.put("message",
                                        "You can't load an empty audio collection!");
                                outputs.add(node);
                                return;
                            }
                            currentPlayer.setNewStatusAlbum(album.getSongs().get(0),
                                    user.getLastSelected(), "album", album,
                                    album.getSongs().get(0).getDuration(),
                                    this.timestamp, 0, false, false);
                            // we add the new data for the song in the wrapped
                            library.addSongForUser(this.username, album.getSongs().get(0));
                        }
                    }
                }
            }
            default -> { }
        }
        node.put("message", "Playback loaded successfully.");
        outputs.add(node);

        user.setLastSelected(null);
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

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public void setPlayers(final ArrayList<Player> players) {
        this.players = players;
    }
}
