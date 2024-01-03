package application.commands.playlists;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.Song;
import application.entities.library.users.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for ShowPlaylists command
 */
@Getter
public final class ShowPlaylists implements Commands {
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
    public ShowPlaylists(final String command, final String username,
                         final Integer timestamp, final Library library) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
    }

    /**
     * Method that applies the command and prints
     * @param objectMapper
     * @param outputs
     */
    @Override
    public void startCommand(final ObjectMapper objectMapper, final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        ArrayNode node1 = objectMapper.createArrayNode();

        User user = library.getUser(username);
        if (user == null) {
            outputs.add(node);
            return;
        }
        if (!user.getPlaylists().isEmpty()) {
            for (Playlist playlist : user.getPlaylists()) {
                ObjectNode node2 = objectMapper.createObjectNode();

                node2.put("name", playlist.getName());
                ArrayList<String> namesList = new ArrayList<>();

                for (Song song : playlist.getSongs()) {
                    namesList.add(song.getName());
                }
                ArrayNode arrayNode;
                if (!playlist.getSongs().isEmpty()) {
                    arrayNode = objectMapper.valueToTree(namesList);
                } else {
                    arrayNode = objectMapper.createArrayNode();
                }
                node2.set("songs", arrayNode);

                if (playlist.isVisibility()) {
                    node2.put("visibility", "public");
                } else {
                    node2.put("visibility", "private");
                }

                node2.put("followers", playlist.getFollowers());
                node1.add(node2);
            }
        }
        node.set("result", node1);
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
}
