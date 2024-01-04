package application.commands.users.artists;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.Song;
import application.entities.library.users.normal.User;
import application.entities.library.users.artist.Album;
import application.entities.library.users.artist.Artist;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

import static application.constants.Constants.THREE;

/**
 * Class for removeAlbum command
 */
@Getter
public final class RemoveAlbum extends PlayerRelatedCommands {
    private String command;
    private String username;
    private Integer timestamp;
    private String name;
    private Library library;
    private ArrayList<Player> players;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param name
     * @param library
     * @param players
     */
    public RemoveAlbum(final String command, final String username,
                       final Integer timestamp, final String name,
                       final Library library, final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.name = name;
        this.library = library;
        this.players = players;
    }

    /**
     * Updates all players to this timestamp
     */
    private void updateAllPlayers() {
        for (Player player: players) {
            User user = library.getUser(player.getUsername());
            if (!player.getName().isEmpty() && !player.isPaused()
                    && user.isStatus()) {
                this.updatePlayer(player, timestamp);
            }
        }
    }

    /**
     * Deletes the album
     */
    private void deleteAlbum() {
        Artist artist = library.getArtist(username);
        Album album = artist.getCertainAlbum(name);
        for (Song song: album.getSongs()) {
            for (User user: library.getUsers()) {
                // we delete each from the likedSongs of all normal users
                // and all the playlists they created
                user.getLikedSongs().remove(song);
                for (Playlist playlist: user.getPlaylists()) {
                    playlist.getSongs().remove(song);
                }
            }
            // we also the delete the song from the library
            library.getSongs().remove(song);
        }
        // in the end we delete the album
        artist.getAlbums().remove(album);
    }

    /**
     * Checks if deleting this album is possible
     * @return
     */
    private boolean checkIfDeletePossible() {
        Artist artist = library.getArtist(username);
        Album album = artist.getCertainAlbum(name);
        for (Player player: players) {
            if (!player.getName().isEmpty()) {
                if (player.getType().equals("song")) {
                    // if any player plays a song from the album
                    // we can't delete
                    for (Song song: album.getSongs()) {
                        if (song.equals(player.getSong())) {
                            return false;
                        }
                    }
                }
                if (player.getType().equals("album")) {
                    // also if any player plays the album
                    if (album.equals(player.getAlbum())) {
                        return false;
                    }
                }
                if (player.getType().equals("playlist")) {
                    // or any player that contains the album
                    for (Song songPlaylist: player.getPlaylist().getSongs()) {
                        for (Song songAlbum: album.getSongs()) {
                            if (songPlaylist.equals(songAlbum)) {
                                return false;
                            }
                        }
                    }
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
        // we stop if the album doesn't exist
        if (!artist.checkIfAlbumExists(name)) {
            node.put("message", username + " doesn't have an album with the given name.");
            outputs.add(node);
            return;
        }
        this.updateAllPlayers();

        if (this.checkIfDeletePossible()) {
            this.deleteAlbum();
            node.put("message", username + " deleted the album successfully.");
        } else {
            node.put("message", username + " can't delete this album.");
        }
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

    public void setName(final String name) {
        this.name = name;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public void setPlayers(final ArrayList<Player> players) {
        this.players = players;
    }
}
