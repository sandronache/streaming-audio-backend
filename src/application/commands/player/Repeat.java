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
 * Class for Repeat command
 */
@Getter
public final class Repeat extends PlayerRelatedCommands {
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
    public Repeat(final String command, final String username,
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
                    "Please load a source before setting the repeat status.");
            outputs.add(node);
            return;
        }
        if (!currentPlayer.isPaused()) {
            this.updatePlayer(currentPlayer, this.timestamp);
        }
        if (currentPlayer.getName().isEmpty()) {
            node.put("message",
                    "Please load a source before setting the repeat status.");
            outputs.add(node);
            return;
        }
        switch (currentPlayer.getRepeat()) {
            case 0 -> {
                currentPlayer.setRepeat(1);
                if (currentPlayer.getType().equals("playlist")
                        || currentPlayer.getType().equals("album")) {
                    node.put("message",
                            "Repeat mode changed to repeat all.");
                } else {
                    node.put("message",
                            "Repeat mode changed to repeat once.");
                }
            }
            case 1 -> {
                currentPlayer.setRepeat(2);
                if (currentPlayer.getType().equals("playlist")
                        || currentPlayer.getType().equals("album")) {
                    node.put("message",
                            "Repeat mode changed to repeat current song.");
                } else {
                    node.put("message",
                            "Repeat mode changed to repeat infinite.");
                }
            }
            default -> {
                currentPlayer.setRepeat(0);
                node.put("message",
                        "Repeat mode changed to no repeat.");
            }
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
