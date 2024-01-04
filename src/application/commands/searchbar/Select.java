package application.commands.searchbar;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.normal.User;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

/**
 * Class for Select command
 */
@Getter
public final class Select implements Commands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private Integer itemNumber;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param itemNumber
     * @param library
     */
    public Select(final String command, final String username, final Integer timestamp,
                  final Integer itemNumber, final Library library) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.itemNumber = itemNumber;
    }

    /**
     * Method that applies the command and prints
     * @param objectMapper
     * @param outputs
     */
    public void startCommand(final ObjectMapper objectMapper, final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        User user = library.getUser(username);
        // if offline user can't do anything
        if (!library.getUser(username).isStatus()) {
            node.put("message", username + " is offline.");
            outputs.add(node);
            // we reset the search
            user.getLastSearch().clear();
            user.setLastSelected(null);
            return;
        }
        // we search is empty we cant select
        if (!user.isSearched()) {
            node.put("message", "Please conduct a search before making a selection.");
            outputs.add(node);
            return;
        }
        // if item is higher we can't select
        if (this.getItemNumber() > user.getLastSearch().size()) {
            node.put("message", "The selected ID is too high.");
            outputs.add(node);
            // reset
            user.getLastSearch().clear();
            user.setLastSelected(null);
            user.setSearched(false);
            return;
        }
        // we select
        user.setLastSelected(user.getLastSearch().get(itemNumber - 1));
        if (user.getTypeLastSearch().equals("artist")) {
            Artist artist = library.getArtist(user.getLastSelected());
            // the user goes on that page
            user.setPage(artist);
            node.put("message", "Successfully selected " + user.getLastSelected() + "'s page.");
            outputs.add(node);
            // reset
            user.getLastSearch().clear();
            user.setSearched(false);
            return;
        }
        if (user.getTypeLastSearch().equals("host")) {
            Host host = library.getHost(user.getLastSelected());
            // the user goes on that page
            user.setPage(host);
            node.put("message", "Successfully selected " + user.getLastSelected() + "'s page.");
            outputs.add(node);
            // reset
            user.getLastSearch().clear();
            user.setSearched(false);
            return;
        }
        if (user.getTypeLastSearch().equals("song")) {
            user.setSongLastSelected(user.getSongsLastSearched().get(
                    itemNumber - 1));
        } else  if (user.getTypeLastSearch().equals("podcast")
                || user.getTypeLastSearch().equals("playlist")
                || user.getTypeLastSearch().equals("album")) {
            user.setArtistLastSelected(user.getArtistLastSearch().get(
                    itemNumber - 1));
        }
        // reset
        user.getLastSearch().clear();
        user.setSearched(false);
        node.put("message", "Successfully selected " + user.getLastSelected() + ".");
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

    public void setItemNumber(final Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }
}
