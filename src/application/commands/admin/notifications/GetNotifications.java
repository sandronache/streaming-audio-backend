package application.commands.admin.notifications;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.normal.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

/**
 * Class for get notifications command
 */
@Getter
public final class GetNotifications implements Commands {
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
    public GetNotifications(final String command, final String username,
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
        // we create the array
        ArrayNode notificationsArray = node.putArray("notifications");
        // we go through the notifications
        for (String notification: user.getNotifications()) {
            ObjectNode temp = objectMapper.createObjectNode();
            // we get the first 2 words for the type
            // split the original string into words
            String[] words = notification.split("\\s+");
            String type = words[0] + " " + words[1];
            // we put the strings
            temp.put("name", type).put("description", notification);
            // we add to the array
            notificationsArray.add(temp);
        }
        // before ending we clear the notifications
        user.clearNotifications();
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
