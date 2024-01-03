package application.commands.admin;

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

/**
 * class for showAlbums command
 */
@Getter
public final class ShowAlbums implements Commands {
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
    public ShowAlbums(final String command, final String username,
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

        // get artist
        Artist artist = library.getArtist(username);
        ArrayNode albumsArray =  objectMapper.createArrayNode();
        for (Album album: artist.getAlbums()) {
            // add album
            ObjectNode node1 = objectMapper.createObjectNode();
            node1.put("name", album.getName());

            ArrayList<String> nameOfSongs = new ArrayList<>();
            for (Song song: album.getSongs()) {
                nameOfSongs.add(song.getName());
            }

            ArrayNode arrayNode;
            if (!nameOfSongs.isEmpty()) {
                arrayNode = objectMapper.valueToTree(nameOfSongs);
            } else {
                arrayNode = objectMapper.createArrayNode();
            }
            node1.set("songs", arrayNode);

            albumsArray.add(node1);
        }
        // print all
        node.set("result", albumsArray);
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
