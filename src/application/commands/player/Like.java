package application.commands.player;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Library;
import application.entities.library.Song;
import application.entities.library.users.normal.User;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Like class is for like command;
 * <p>
 *     It holds a field with the likes for all users so the command can add a new like
 * </p>
 */
@Getter
public final class Like extends PlayerRelatedCommands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private ArrayList<Player> players;

    /**
     *  Constructor for Like
     * @param command for command name
     * @param username
     * @param timestamp for new timestamp
     * @param library
     * @param players
     */
    public Like(final String command, final String username,
                final Integer timestamp, final Library library,
                final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.players = players;
    }

    /**
     * Print method that prints and executes the command
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

        // we check of the status of this user's player
        Player currentPlayer = this.getCurrentPlayer(players, this.getUsername());
        if (currentPlayer == null || currentPlayer.getName().isEmpty()) {
            node.put("message",
                    "Please load a source before liking or unliking.");
            outputs.add(node);
            return;
        }

        if (currentPlayer.getType().equals("podcast")) {
            node.put("message",
                    "Loaded source is not a song.");
            outputs.add(node);
            return;
        }
        // we update the player
        if (!currentPlayer.isPaused()) {
            this.updatePlayer(currentPlayer, this.timestamp);
        }

        if (currentPlayer.getName().isEmpty()) {
            node.put("message",
                    "Please load a source before liking or unliking.");
            outputs.add(node);
            return;
        }

        User user = library.getUser(username);
        if (user == null) {
            outputs.add(node);
            return;
        }
        if (user.getLikedSongs().isEmpty()) {
            user.addLikedSong(currentPlayer.getSong());
            currentPlayer.getSong().addLike();
            node.put("message", "Like registered successfully.");
            outputs.add(node);
            return;
        }
        Song copySong = null;
        for (Song song : user.getLikedSongs()) {
            if (song.equals(currentPlayer.getSong())) {
                copySong = song;
                break;
            }
        }
        if (copySong == null) {
            user.getLikedSongs().add(currentPlayer.getSong());
            currentPlayer.getSong().addLike();
            node.put("message",
                    "Like registered successfully.");
        } else {
            user.getLikedSongs().remove(currentPlayer.getSong());
            currentPlayer.getSong().dislike();
            node.put("message",
                    "Unlike registered successfully.");
        }
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
}
