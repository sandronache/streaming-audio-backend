package application.commands.admin.recommendations;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.Song;
import application.entities.library.users.normal.User;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for load recommendation command
 */
@Getter
public final class LoadRecommendation extends PlayerRelatedCommands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private ArrayList<Player> players;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param library
     * @param players
     */
    public LoadRecommendation(final String command, final String username,
                      final Integer timestamp, final Library library,
                      final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.players = players;
    }

    /**
     * The starting point for the command
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
        // we check if he s offline
        if (!user.isStatus()) {
            node.put("message", username + " is offline.");
            outputs.add(node);
            return;
        }
        // we check if we have recommendations to load
        if (user.getLastRecommendation() == 0) {
            node.put("message", "No recommendations available.");
            outputs.add(node);
            return;
        }
        // we update the player if possible
        Player currentPlayer = this.getCurrentPlayer(players, this.getUsername());
        if (currentPlayer == null) {
            return;
        }
        if (currentPlayer != null && !currentPlayer.getName().isEmpty()
               && !currentPlayer.isPaused()) {
            this.updatePlayer(currentPlayer, this.timestamp);
            currentPlayer.setTimestamp(timestamp);
        }
        // now we load the recommendation
        if (user.getLastRecommendation() == 1) {
            Song tempSong = user.getRecommendedSongs()
                    .get(user.getRecommendedSongs().size() - 1);
            currentPlayer.setNewStatusSong(tempSong,
                    tempSong.getName(), "song",
                    tempSong.getDuration(), this.timestamp,
                    0, false, false);
            // we add the new data for the song in the wrapped
            library.addSongForUser(this.username, tempSong);
        } else {
            Playlist playlist = user.getRecommendedPlaylists()
                    .get(user.getRecommendedPlaylists().size() - 1);
            if (playlist.getSongs().isEmpty()) {
                node.put("message",
                        "You can't load an empty audio collection!");
                outputs.add(node);
                return;
            }
            currentPlayer.setNewStatusPlaylist(playlist.getSongs().get(0),
                    user.getLastSelected(), "playlist", playlist,
                    playlist.getSongs().get(0).getDuration(),
                    this.timestamp, 0, false, false);
            // we add the new data for the song in the wrapped
            library.addSongForUser(this.username, playlist.getSongs().get(0));
        }
        node.put("message", "Playback loaded successfully.");
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

    public void setPlayers(final ArrayList<Player> players) {
        this.players = players;
    }

}
