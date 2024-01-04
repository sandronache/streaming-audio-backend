package application.commands.users.normal.pages;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.normal.User;
import application.entities.pages.HomePage;
import application.entities.pages.LikedContentPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

/**
 * Class for changePage command
 */
@Getter
public final class ChangePage implements Commands {
    private String command;
    private String username;
    private Integer timestamp;
    private String nextPage;
    private Library library;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param nextPage
     * @param library
     */
    public ChangePage(final String command, final String username,
                      final Integer timestamp, final String nextPage,
                      final Library library) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.nextPage = nextPage;
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
        //we get the user
        User user = library.getUser(username);
        // set page to home
        if (nextPage.equals("Home")) {
            user.setPage(new HomePage());
            node.put("message", username + " accessed " + nextPage
                    + " successfully.");
            outputs.add(node);
            return;
        }
        // set page to liked content page
        if (nextPage.equals("LikedContent")) {
            user.setPage(new LikedContentPage());
            node.put("message", username + " accessed " + nextPage
                    + " successfully.");
            outputs.add(node);
            return;
        }
        node.put("message", username + " is trying to access a non-existent page.");
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

    public void setNextPage(final String nextPage) {
        this.nextPage = nextPage;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }
}
