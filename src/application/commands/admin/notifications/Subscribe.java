package application.commands.admin.notifications;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import application.entities.library.users.normal.User;
import application.entities.pages.typevisitor.TypeVisitor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

/**
 * Class for subscribe command
 */
@Getter
public final class Subscribe implements Commands {
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
    public Subscribe(final String command, final String username,
                    final Integer timestamp, final Library library) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
    }

    /**
     * The starting point for this command
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
        if (library.getUser(username) == null) {
            node.put("message", "The username " + username + " doesn't exists.");
            outputs.add(node);
            return;
        }
        // we get the user
        User user =  library.getUser(username);
        // we check if we are on the page of an artist or host
        TypeVisitor visitor = new TypeVisitor();
        if (!user.accept(visitor).equals("host")
                && !user.accept(visitor).equals("artist")) {
            node.put("message",
                    "To subscribe you need to be on the page of an artist or host.");
            outputs.add(node);
            return;
        }
        // if we are on the page of an artist
        if (user.accept(visitor).equals("artist")) {
            Artist tempArtist = (Artist) user.getPage();
            if (!tempArtist.unsubscribeOrSubscribeUser(user)) {
                node.put("message", username +  " unsubscribed from "
                + tempArtist.getUsername() + " successfully.");
            } else {
                node.put("message", username +  " subscribed to "
                        + tempArtist.getUsername() + " successfully.");
            }
        } else {
            // if we are on the page of an host
            Host tempHost = (Host) user.getPage();
            if (!tempHost.unsubscribeOrSubscribeUser(user)) {
                node.put("message", username +  " unsubscribed from "
                        + tempHost.getUsername() + " successfully.");
            } else {
                node.put("message", username +  " subscribed to "
                        + tempHost.getUsername() + " successfully.");
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

    public void setLibrary(final Library library) {
        this.library = library;
    }

}
