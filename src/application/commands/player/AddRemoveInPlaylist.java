package application.commands.player;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.Song;
import application.entities.library.users.normal.User;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class for AddRemoveInPlaylist command
 */
@Getter
public final class AddRemoveInPlaylist extends PlayerRelatedCommands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private ArrayList<Player> players;
    private Integer playlistId;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param playlistId
     * @param library
     * @param players
     */
    public AddRemoveInPlaylist(final String command, final String username,
                               final Integer timestamp, final Integer playlistId,
                               final Library library, final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.playlistId = playlistId;
        this.players = players;
    }

    /**
     * Method applies command and prints
     * @param objectMapper
     * @param outputs
     */
    @Override
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

        Player currentPlayer = this.getCurrentPlayer(players, this.getUsername());
        if (currentPlayer == null || currentPlayer.getName().isEmpty()) {
            node.put("message",
                    "Please load a source before adding to or removing from the playlist.");
            outputs.add(node);
            return;
        }

        if (!currentPlayer.getType().equals("song")
                && !currentPlayer.getType().equals("playlist")
                && !currentPlayer.getType().equals("album")) {
            node.put("message", "The loaded source is not a song.");
            outputs.add(node);
            return;
        }

        if (!currentPlayer.isPaused()) {
            this.updatePlayer(currentPlayer, this.timestamp);
        }

        if (currentPlayer.getName().isEmpty()) {
            node.put("message",
                    "Please load a source before adding to or removing from the playlist.");
            outputs.add(node);
            return;
        }

        User user = library.getUser(username);
        if (user == null) {
            outputs.add(node);
            return;
        }
        if (!user.getPlaylists().isEmpty()) {
            for (Playlist playlist : user.getPlaylists()) {
                if (Objects.equals(playlist.getId(), this.playlistId)) {
                    Song copySong = null;
                    for (int i = 0; i < playlist.getSongs().size(); i++) {
                        if (playlist.getSongs().get(i).equals(
                                currentPlayer.getSong())) {
                            copySong = playlist.getSongs().get(i);
                        }
                    }
                    if (copySong == null) {
                        playlist.getSongs().add(currentPlayer.getSong());
                        node.put("message",
                                "Successfully added to playlist.");
                        outputs.add(node);
                        return;
                    } else {
                        playlist.getSongs().remove(currentPlayer.getSong());
                        node.put("message",
                                "Successfully removed from playlist.");
                        outputs.add(node);
                        return;
                    }
                }
            }
        }
        node.put("message", "The specified playlist does not exist.");
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

    public void setPlaylistId(final Integer playlistId) {
        this.playlistId = playlistId;
    }
}
