package application.commands.playlists;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.users.normal.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.Objects;

/**
 * Class for SwitchVisibility command
 */
@Getter
public final class SwitchVisibility implements Commands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private Integer playlistId;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param playlistId - the id of the playlist
     * @param library
     */
    public SwitchVisibility(final String command, final String username,
                            final Integer timestamp, final Integer playlistId,
                            final Library library) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.playlistId = playlistId;
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

        if (library.typeOfUser(username) == 1
                && !library.getUser(username).isStatus()) {
            node.put("message", username + " is offline.");
            outputs.add(node);
            return;
        }

        User user = library.getUser(username);
        if (user == null) {
            outputs.add(node);
            return;
        }

        if (!user.getPlaylists().isEmpty()) {
            for (Playlist playlist : user.getPlaylists()) {
                if (this.playlistId > user.getPlaylists().get(
                        user.getPlaylists().size() - 1).getId()) {
                    node.put("message",
                            "The specified playlist ID is too high.");
                    outputs.add(node);
                    return;
                }
                if (Objects.equals(playlist.getId(), this.playlistId)) {
                    if (playlist.isVisibility()) {
                        playlist.setVisibility(false);
                        node.put("message",
                                "Visibility status updated successfully to private.");
                    } else {
                        playlist.setVisibility(true);
                        node.put("message",
                                "Visibility status updated successfully to public.");
                    }
                    outputs.add(node);
                    return;
                }
            }
        }
        node.put("message", "The specified playlist ID is too high.");
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

    public void setPlaylistId(final Integer playlistId) {
        this.playlistId = playlistId;
    }

}
