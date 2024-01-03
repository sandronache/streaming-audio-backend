package application.commands.users.hosts;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.host.Host;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

/**
 * Class for removeAnnouncement command
 */
@Getter
public final class RemoveAnnouncement implements Commands {
    private String command;
    private String username;
    private Integer timestamp;
    private String name;
    private Library library;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param name
     * @param library
     */
    public RemoveAnnouncement(final String command, final String username,
                       final Integer timestamp, final String name,
                       final Library library) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.name = name;
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
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        // we check if the username exists
        if (library.typeOfUser(username) == 0) {
            node.put("message", "The username "
                    + username + " doesn't exist.");
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
        // we check if this host has this announcement
        if (!host.checkIfAnnouncementExists(name)) {
            node.put("message", username
                    + " has no announcement with the given name.");
            outputs.add(node);
            return;
        }

        // if yes we remove it
        host.removeAnnouncement(name);
        node.put("message", username
                + " has successfully deleted the announcement.");
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

}
