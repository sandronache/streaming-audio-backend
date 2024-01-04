package application.commands.users.hosts;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Library;
import application.entities.library.Podcast;
import application.entities.library.users.normal.User;
import application.entities.library.users.host.Host;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for removePodcast command
 */
@Getter
public final class RemovePodcast extends PlayerRelatedCommands {
    private String command;
    private String username;
    private Integer timestamp;
    private String name;
    private Library library;
    private ArrayList<Player> players;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param name
     * @param library
     * @param players
     */
    public RemovePodcast(final String command, final String username,
                       final Integer timestamp, final String name,
                       final Library library, final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.name = name;
        this.library = library;
        this.players = players;
    }

    /**
     * Updates all players to this timestamp
     */
    private void updateAllPlayers() {
        for (Player player: players) {
            User user = library.getUser(player.getUsername());
            if (!player.getName().isEmpty() && !player.isPaused()
                    && user.isStatus()) {
                this.updatePlayer(player, timestamp);
            }
        }
    }

    /**
     * Deletes a podcast
     */
    public void deletePodcast() {
        Host host = library.getHost(username);
        Podcast podcast = host.getPodcast(name);
        int indexLibrary = 0;
        // first we delete the status in the podcast status from
        // each existing player (for my implementation)
        for (int i = 0; i < library.getPodcasts().size(); i++) {
            if (library.getPodcasts().get(i).equals(podcast)) {
                indexLibrary = i;
                break;
            }
        }
        // and then we remove the podcast from the library
        for (Player player: players) {
            player.getSituationPodcasts().remove(indexLibrary);
        }
        library.getPodcasts().remove(podcast);
        host.getPodcasts().remove(podcast);
    }

    /**
     * Checks if the podcast can be deleted
     * @return
     */
    private boolean checkIfDeletePossible() {
        for (Player player: players) {
            if (!player.getName().isEmpty()
                && player.getType().equals("podcast")) {
                if (player.getName().equals(name)) {
                    return false;
                }
            }
        }
        return true;
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

        // we check if the username exists
        if (library.typeOfUser(username) == 0) {
            node.put("message", "The username " + username + " doesn't exist.");
            outputs.add(node);
            return;
        }

        // we check if this username is a host
        if (library.typeOfUser(username) != 2) {
            node.put("message", username + " is not a host.");
            outputs.add(node);
            return;
        }

        Host host = library.getHost(username);
        // we stop if the album doesn't exist
        if (!host.checkIfPodcastExists(name)) {
            node.put("message", username
                    + " doesn't have a podcast with the given name.");
            outputs.add(node);
            return;
        }

        this.updateAllPlayers();

        if (this.checkIfDeletePossible()) {
            this.deletePodcast();
            node.put("message", username
                    + " deleted the podcast successfully.");
        } else {
            node.put("message", username
                    + " can't delete this podcast.");
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

    public void setName(final String name) {
        this.name = name;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public void setPlayers(final ArrayList<Player> players) {
        this.players = players;
    }

}
