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
import java.util.Random;

/**
 * Class for update recommendations command
 */
@Getter
public final class UpdateRecommendations extends PlayerRelatedCommands {
    private String command;
    private String username;
    private Integer timestamp;
    private String recommendationType;
    private Library library;
    private ArrayList<Player> players;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param library
     */
    public UpdateRecommendations(final String command, final String username,
                    final Integer timestamp, final String recommendationType,
                     final Library library, final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.recommendationType = recommendationType;
        this.library = library;
        this.players = players;
    }

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
        } else if (library.typeOfUser(username) != 1) {
            node.put("message", username + " is not a normal user.");
            outputs.add(node);
            return;
        }
        // we get the user
        User user =  library.getUser(username);
        // we first update the player
        Player currentPlayer = this.getCurrentPlayer(players, this.getUsername());
        if (currentPlayer == null || currentPlayer.getName().isEmpty()
                || currentPlayer.isPaused()) {
            node.put("message",
                    "Please load a source before wanting to go to an site");
            outputs.add(node);
            return;
        }
        this.updatePlayer(currentPlayer, this.timestamp);
        currentPlayer.setTimestamp(timestamp);
        // start cases
        if (this.recommendationType.equals("random_song")) {
            if (currentPlayer.getSong().getDuration()
                    - currentPlayer.getRemainedTime() >= 30) {
                int seed = currentPlayer.getSong().getDuration()
                            - currentPlayer.getRemainedTime();
                ArrayList<Song> sameGenreSongs =  new ArrayList<>();
                for (Song songLibrary: library.getSongs()) {
                    if (songLibrary.getGenre().equals(currentPlayer.getSong().getGenre())) {
                        sameGenreSongs.add(songLibrary);
                    }
                }
                Random random = new Random(seed);
                Song chosenSong = sameGenreSongs.get(random.nextInt(sameGenreSongs.size()));
                user.addRecommendedSong(chosenSong);
            } else {
                node.put("message",
                        "No new recommendations were found.");
                outputs.add(node);
                return;
            }
        } else if (this.recommendationType.equals("random_playlist")) {
            user.addRecommendedPlaylist(new Playlist(false, username
                        + "'s recommendations", 0, 0 , 0));
        } else {
            user.addRecommendedPlaylist(new Playlist(false,
                    currentPlayer.getSong().getArtist() + " Fan Club recommendations",
                    0, 0 , 0));
        }

        node.put("message", "The recommendations for user "
                + username + " have been updated successfully.");
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

    public void setRecommendationType(final String recommendationType) {
        this.recommendationType = recommendationType;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public void setPlayers(final ArrayList<Player> players) {
        this.players = players;
    }

}
