package application.commands.admin.premium;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Library;
import application.entities.library.users.normal.User;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for cancel premium command
 */
@Getter
public final class CancelPremium extends PlayerRelatedCommands {
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
    public CancelPremium(final String command, final String username,
                     final Integer timestamp, final Library library,
                     final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.players = players;
    }

    /**
     * The starting point for this command
     * @param objectMapper
     * @param outputs
     */
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
        }
        // we get the user
        User user =  library.getUser(username);
        // we update the player
        Player currentPlayer = this.getCurrentPlayer(players, username);
        if (currentPlayer != null && !currentPlayer.getName().isEmpty()
                && !currentPlayer.isPaused()) {
            this.updatePlayer(currentPlayer, timestamp);
        }
        // we check if the user is a premium user
        if (!user.isPremium()) {
            node.put("message",  username + " is not a premium user.");
            outputs.add(node);
            return;
        }
        // cancel subscription and make the required calculations
        user.cancelPremiumManagement(library);
        node.put("message",  username + " cancelled the subscription successfully.");
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
