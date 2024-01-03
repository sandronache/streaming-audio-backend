package application.commands.statistics;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.User;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for getAllUsers command
 */
@Getter
public final class GetAllUsers implements Commands {
    private String command;
    private Integer timestamp;
    private Library library;

    /**
     * Constructor
     * @param command
     * @param timestamp
     * @param library
     */
    public GetAllUsers(final String command, final Integer timestamp,
                       final Library library) {
        this.command = command;
        this.timestamp = timestamp;
        this.library = library;
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
        node.put("timestamp", this.getTimestamp());
        // we add all types of users (the username) in a list
        ArrayList<String> usersToPrint = new ArrayList<>();
        // normal user
        for (User user: library.getUsers()) {
            usersToPrint.add(user.getUsername());
        }
        // artist
        for (Artist artist: library.getArtists()) {
            usersToPrint.add(artist.getUsername());
        }
        // host
        for (Host host: library.getHosts()) {
            usersToPrint.add(host.getUsername());
        }
        //
        ArrayNode arrayNode;
        if (!usersToPrint.isEmpty()) {
            arrayNode = objectMapper.valueToTree(usersToPrint);
        } else {
            arrayNode = objectMapper.createArrayNode();
        }
        node.set("result", arrayNode);
        outputs.add(node);
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public void setTimestamp(final Integer timestamp) {
        this.timestamp = timestamp;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

}
