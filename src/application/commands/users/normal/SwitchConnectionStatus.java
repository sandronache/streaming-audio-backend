package application.commands.users.normal;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Library;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for switchConnectionStatus
 */
@Getter
public final class SwitchConnectionStatus extends PlayerRelatedCommands {
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
     * @param players
     */
    public SwitchConnectionStatus(final String command, final String username,
                                  final Integer timestamp, final Library library,
                                  final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.players = players;
    }

    /**
     * Starting point of the command
     * Makes the required changes and prints accordingly
     * @param objectMapper
     * @param outputs
     */
    @Override
    public void startCommand(final ObjectMapper objectMapper, final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        if (library.typeOfUser(username) == 0) {
            node.put("message",
                    "The username " + username + " doesn't exist.");
            outputs.add(node);
            return;
        }

        if (library.typeOfUser(username) != 1) {
            node.put("message",
                    username + " is not a normal user.");
            outputs.add(node);
            return;
        }

        Player currentPlayer = this.getCurrentPlayer(players, username);

        if (library.getUser(username).isStatus()) {
            if (currentPlayer != null && !currentPlayer.getName().isEmpty()
                    && !currentPlayer.isPaused()) {
                this.updatePlayer(currentPlayer, this.timestamp);
            }
        } else if (currentPlayer != null) {
                currentPlayer.setTimestamp(this.timestamp);
        }
        library.getUser(username).switchStatus();
        node.put("message",
                username + " has changed status successfully.");
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

}
