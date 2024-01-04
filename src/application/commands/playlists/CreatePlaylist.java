package application.commands.playlists;


import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.users.normal.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

/**
 * Class for CreatePlaylist command
 */
@Getter
public final class CreatePlaylist implements Commands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private String playlistName;
    private Integer globalPlaylistId;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param playlistName
     * @param globalPlaylistId
     * @param library
     */
    public CreatePlaylist(final String command, final String username, final Integer timestamp,
                          final String playlistName,
                          final Integer globalPlaylistId, final Library library) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.playlistName = playlistName;
        this.globalPlaylistId = globalPlaylistId;
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
                if (playlist.getName().equals(this.playlistName)) {
                    node.put("message",
                            "A playlist with the same "
                                    + "name already exists.");
                    outputs.add(node);
                    return;
                }
            }
        }
        int id;
        if (globalPlaylistId != null) {
            globalPlaylistId++;
        } else {
            globalPlaylistId = 0;
        }
        if (user.getPlaylists().isEmpty()) {
            id = 1;
        } else {
            id = user.getPlaylists().get(
                    user.getPlaylists().size() - 1).getId() + 1;
        }
        Playlist newPlaylist = new Playlist(true,
                this.playlistName, id, 0, globalPlaylistId);
        user.addPlaylist(newPlaylist);
        node.put("message", "Playlist created successfully.");
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

    public void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    public void setGlobalPlaylistId(final Integer globalPlaylistId) {
        this.globalPlaylistId = globalPlaylistId;
    }
}
