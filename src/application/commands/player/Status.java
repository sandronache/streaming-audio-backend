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
 * Class for Status command
 */
@Getter
public final class Status extends PlayerRelatedCommands {
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
    public Status(final String command, final String username, final Integer timestamp,
                  final Library library, final ArrayList<Player> players) {
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

        Player currentPlayer = this.getCurrentPlayer(players, this.getUsername());
        if (currentPlayer == null) {
            return;
        }

        if (!currentPlayer.getName().isEmpty() && !currentPlayer.isPaused()
                && library.getUser(username).isStatus()) {
            this.updatePlayer(currentPlayer, this.timestamp);
        }

        ObjectNode node1 = objectMapper.createObjectNode();

        switch (currentPlayer.getType()) {
            case "podcast" -> {
                if (currentPlayer.episodeName() == null) {
                    node1.put("name", "");
                } else {
                    node1.put("name", currentPlayer.episodeName());
                }
            }
            case "playlist", "album" -> {
                if (currentPlayer.getName().isEmpty()) {
                    node1.put("name", "");
                } else {
                    node1.put("name", currentPlayer.getSong().getName());
                }
            }
            default -> node1.put("name", currentPlayer.getName());
        }

        switch (currentPlayer.getType()) {
            case "song", "playlist", "album" -> node1.put("remainedTime",
                    currentPlayer.getRemainedTime());
            default -> node1.put("remainedTime", currentPlayer.remainedTimePodcast());
        }

        if (currentPlayer.getRepeat() == 0) {
            node1.put("repeat", "No Repeat");
        } else if (currentPlayer.getRepeat() == 1) {
            if (currentPlayer.getType().equals("playlist")
                    || currentPlayer.getType().equals("album")) {
                node1.put("repeat", "Repeat All");
            } else {
                node1.put("repeat", "Repeat Once");
            }
        } else if (currentPlayer.getRepeat() == 2) {
            if (currentPlayer.getType().equals("playlist")
                    || currentPlayer.getType().equals("album")) {
                node1.put("repeat", "Repeat Current Song");
            } else {
                node1.put("repeat", "Repeat Infinite");
            }
        }
        node1.put("shuffle", currentPlayer.isShuffle());
        node1.put("paused", currentPlayer.isPaused());
        node.set("stats", node1);
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
