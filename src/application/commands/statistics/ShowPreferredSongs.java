package application.commands.statistics;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.Song;
import application.entities.library.users.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for ShowPreferredSongs command;
 */
@Getter
public final class ShowPreferredSongs implements Commands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param library
     */
    public ShowPreferredSongs(final String command, final String username,
                              final Integer timestamp, final Library library) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
    }

    /**
     * Method that builds the list of liked songs and prints it
     * @param objectMapper
     * @param outputs
     */
    public void startCommand(final ObjectMapper objectMapper, final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        // we print the liked songs for this user
        User user = library.getUser(username);
        if (user == null) {
            outputs.add(node);
            return;
        }

        ArrayNode arrayNode;
        if (!user.getLikedSongs().isEmpty()) {
            // we add the songs names in a list
            ArrayList<String> printList = new ArrayList<>();
            for (Song song : user.getLikedSongs()) {
                printList.add(song.getName());
            }
            // we print it
            arrayNode = objectMapper.valueToTree(printList);
            node.set("result", arrayNode);
            outputs.add(node);
            return;
        }
        arrayNode = objectMapper.createArrayNode();
        node.set("result", arrayNode);
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
