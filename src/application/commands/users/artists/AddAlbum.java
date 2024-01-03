package application.commands.users.artists;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.Song;
import application.entities.library.users.artist.Album;
import application.entities.library.users.artist.Artist;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

import static application.constants.Constants.THREE;

/**
 * Class for addAlbum command
 */
@Getter
public final class AddAlbum implements Commands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private String name;
    private Integer releaseYear;
    private String description;
    private ArrayList<Song> songs;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param library
     * @param name
     * @param releaseYear
     * @param description
     * @param songs
     */
    public AddAlbum(final String command, final String username, final Integer timestamp,
                    final Library library, final String name, final Integer releaseYear,
                    final String description, final ArrayList<Song> songs) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.name = name;
        this.releaseYear = releaseYear;
        this.description = description;
        this.songs = songs;
    }

    /**
     * Method that checks if the songs in the new album are all
     * different
     * @return - true/false
     */
    private boolean songsAllDifferent() {
        for (int i = 0; i < songs.size(); i++) {
            for (int j = 0; j < songs.size(); j++) {
                if (i != j && (songs.get(i).equals(songs.get(j))
                    || songs.get(i).getName().equals(songs.get(j).getName()))) {
                    return false;
                }
            }
        }
        return true;
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
        // we check if this artist already has this album
        if (artist.checkIfAlbumExists(name)) {
            node.put("message", username + " has another album with the same name.");
            outputs.add(node);
            return;
        }

        // we check if the new album has songs duplicated
        if (!this.songsAllDifferent()) {
            node.put("message", username + " has the same song at least twice in this album.");
            outputs.add(node);
            return;
        }

        // if all the checks were good we add the new album
        library.addAlbumSongs(songs);
        Album newAlbum = new Album(name, releaseYear, description, songs);
        artist.getAlbums().add(newAlbum);
        node.put("message", username + " has added new album successfully.");
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

    public void setReleaseYear(final Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }
}
