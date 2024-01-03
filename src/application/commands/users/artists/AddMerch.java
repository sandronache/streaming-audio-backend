package application.commands.users.artists;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.artist.Merch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import static application.constants.Constants.THREE;

/**
 * Class for addMerch command
 */
@Getter
public final class AddMerch implements Commands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private String name;
    private String description;
    private Integer price;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param library
     * @param name
     * @param description
     * @param price
     */
    public AddMerch(final String command, final String username, final Integer timestamp,
                    final Library library, final String name, final String description,
                    final Integer price) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.name = name;
        this.description = description;
        this.price = price;
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

        // we check if this username is an artist
        if (library.typeOfUser(username) != THREE) {
            node.put("message", username + " is not an artist.");
            outputs.add(node);
            return;
        }

        Artist artist = library.getArtist(username);
        // we check if this artist already has this merch
        if (artist.checkIfMerchExists(name)) {
            node.put("message", username + " has merchandise with the same name.");
            outputs.add(node);
            return;
        }

        // we check if the price is negative
        if (price < 0) {
            node.put("message", "Price for merchandise can not be negative.");
            outputs.add(node);
            return;
        }
        // if all the checks were good we add the new merch
        Merch newMerch = new Merch(name, description, price);
        artist.getMerchandise().add(newMerch);
        node.put("message", username + " has added new merchandise successfully.");
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

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setPrice(final Integer price) {
        this.price = price;
    }
}
