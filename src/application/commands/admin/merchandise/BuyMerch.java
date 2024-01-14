package application.commands.admin.merchandise;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.normal.User;
import application.entities.pages.typevisitor.TypeVisitor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

/**
 * Class for buy merch command
 */
@Getter
public final class BuyMerch implements Commands {
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
    public BuyMerch(final String command, final String username,
                    final Integer timestamp, final String name,
                    final Library library) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.name = name;
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
        // we check if we are on the page of an artist
        TypeVisitor visitor = new TypeVisitor();
        if (!user.accept(visitor).equals("artist")) {
            node.put("message", "Cannot buy merch from this page.");
            outputs.add(node);
            return;
        }
        // we check if the merch exists, and if true we add it to our purchases
        Artist tempArtist = (Artist) user.getPage();
        if (!tempArtist.checkIfMerchExists(name)) {
            node.put("message", "The merch " + name + " doesn't exist.");
            outputs.add(node);
            return;
        }
        // we add the new merch
        user.getBoughtMerchandise().add(name);
        // we add the purchase to the account of the artist
        library.addRevenueMerchandise((double) tempArtist.getPriceMerch(name),
                tempArtist.getUsername());
        node.put("message", username + " has added new merch successfully.");
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

    public void setName(final String name) {
        this.name = name;
    }

}
