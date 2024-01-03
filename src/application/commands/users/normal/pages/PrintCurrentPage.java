package application.commands.users.normal.pages;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.User;
import application.entities.pages.visitor.DisplayVisitor;
import application.entities.pages.visitor.UpdateVisitor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

/**
 * Class for printCurrentPage command
 */
@Getter
public final class PrintCurrentPage implements Commands {
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
    public PrintCurrentPage(final String command, final String username,
                        final Integer timestamp, final Library library) {
        this.command = command;
        this.username = username;
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
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        if (library.typeOfUser(username) == 1
                && !library.getUser(username).isStatus()) {
            node.put("message", username + " is offline.");
            outputs.add(node);
            return;
        }

        User user = library.getUser(username);
        // firstly we update the page
        UpdateVisitor updateVisitor = new UpdateVisitor(user);
        user.accept(updateVisitor);
        // and then we display it
        DisplayVisitor displayVisitor = new DisplayVisitor(node, library);
        user.accept(displayVisitor);
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
