package application.commands.player;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Library;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for PlayPause command
 */
@Getter
public final class PlayPause extends PlayerRelatedCommands {
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
     * @param players
     */
    public PlayPause(final String command, final String username,
                     final Integer timestamp, final Library library,
                     final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
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
                    "Please load a source before attempting to pause or resume playback.");
            outputs.add(node);
            return;
        }
        if (currentPlayer.isPaused()) {
            currentPlayer.setTimestamp(this.getTimestamp());
            currentPlayer.setPaused(false);
            node.put("message", "Playback resumed successfully.");
            outputs.add(node);
            return;
        }
        this.updatePlayer(currentPlayer, this.timestamp);
        if (currentPlayer.getName().isEmpty()) {
            node.put("message",
                    "Please load a source before attempting to pause or resume playback.");
            outputs.add(node);
            return;
        }
        currentPlayer.setPaused(true);
        node.put("message", "Playback paused successfully.");
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

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public void setPlayers(final ArrayList<Player> players) {
        this.players = players;
    }
}
