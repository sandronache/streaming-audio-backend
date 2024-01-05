package application.commands.users.artists;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.artist.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import static application.constants.Constants.THREE;

/**
 * Class for addEvent command
 */
@Getter
public final class AddEvent implements Commands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private String name;
    private String description;
    private String date;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param library
     * @param name
     * @param description
     * @param date
     */
    public AddEvent(final String command, final String username, final Integer timestamp,
                    final Library library, final String name, final String description,
                    final String date) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.name = name;
        this.description = description;
        this.date = date;
    }

    /**
     * Method checks if the date of the event is valid
     * @return
     */
    private boolean isValidDate() {
        if (date.matches("\\d{2}-\\d{2}-\\d{4}")) {
            int day = Integer.parseInt(date.substring(0, 2));
            int month = Integer.parseInt(date.substring(THREE, 5));
            int year = Integer.parseInt(date.substring(6, 10));

            return (month <= 12 && day <= 31
                    && (month != 2 || day <= 28)
                    && year >= 1900 && year <= 2023);
        }
        return false;
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
        // we check if this artist already has this event
        if (artist.checkIfEventExists(name)) {
            node.put("message", username + " has another event with the same name.");
            outputs.add(node);
            return;
        }
        // we check if the date is valid
        if (!this.isValidDate()) {
            node.put("message", "Event for " + username + " does not have a valid date.");
            outputs.add(node);
            return;
        }
        // if all the checks were good we add the new event
        Event newEvent = new Event(name, description, date);
        artist.getEvents().add(newEvent);
        // we also send notification if possible
        artist.sendNotificationIfPossible(2);
        node.put("message", username + " has added new event successfully.");
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

    public void setDate(final String date) {
        this.date = date;
    }
}
