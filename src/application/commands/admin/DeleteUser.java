package application.commands.admin;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.Podcast;
import application.entities.library.Song;
import application.entities.library.users.artist.Album;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import application.entities.library.users.normal.User;
import application.entities.pages.typevisitor.TypeVisitor;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

import static application.constants.Constants.THREE;

/**
 * Class for deleteUser command
 */
@Getter
public final class DeleteUser extends PlayerRelatedCommands {
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
    public DeleteUser(final String command, final String username,
                      final Integer timestamp, final Library library,
                      final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.players = players;
    }

    /**
     * The deleting process for the normal user
     * @return
     */
    private boolean deleteForNormalUser() {
        User user = library.getUser(username);
        // if the user is a premium user we can't remove it
        if (user.isPremium()) {
            return false;
        }
        // we check if any playlist plays any of this users playlist
        for (Player player: players) {
            if (!player.getUsername().equals(username)) {
                if (!player.getName().isEmpty()
                    && player.getType().equals("playlist")) {
                    for (Playlist playlist: user.getPlaylists()) {
                        if (playlist.isVisibility()
                                && playlist.equals(player.getPlaylist())) {
                            return false;
                        }
                    }
                }
            }
        }
        // if the user is on the page of a host or an artist
        // we cant delete it
        TypeVisitor visitor = new TypeVisitor();
        if (user.accept(visitor).equals("host")
        || user.accept(visitor).equals("artist")) {
            return false;
        }
        // if we got here we can delete the user
        // first we delete the playlists of this user
        // and all the connections
        for (Playlist playlist: user.getPlaylists()) {
            for (String follower: playlist.getTheFollowers()) {
                User theFollower = library.getUser(follower);
                theFollower.getFollowedPlaylists().remove(playlist);
            }
        }
        // now we get each liked song and we delete the like
        for (Song song: user.getLikedSongs()) {
            song.dislike();
        }
        // now we get each followed playlist and we remove
        // this user as a follower
        for (Playlist playlist: user.getFollowedPlaylists()) {
            playlist.setFollowers(playlist.getFollowers() - 1);
            playlist.getTheFollowers().remove(user.getUsername());
        }
        library.getUsers().remove(user);
        return true;
    }

    /**
     * The deleting process for an artist
     * @return
     */
    private boolean deleteForArtist() {
        Artist artist = library.getArtist(username);
        // we check if any normal user has loaded
        // any of his albums or any of the songs from
        // any of those
        for (Player player: players) {
            if (!player.getName().isEmpty()) {
                if (player.getType().equals("song")) {
                    if (artist.checkIfSongExists(player.getSong())) {
                        return false;
                    }
                }
                if (player.getType().equals("album")) {
                    if (artist.checkIfAlbumExists(player.getAlbum().getName())) {
                        return false;
                    }
                }
                if (player.getType().equals("playlist")) {
                    for (Song songPlayer: player.getPlaylist().getSongs()) {
                        if (artist.checkIfSongExists(songPlayer)) {
                            return false;
                        }
                    }
                }
            }
        }
        // now we check if any of the normal users are on the page
        // of this artist
        for (User user: library.getUsers()) {
            TypeVisitor visitor = new TypeVisitor();
            if (user.accept(visitor).equals("artist")) {
                Artist castedArtist = (Artist) user.getPage();
                if (castedArtist.equals(artist)) {
                    return false;
                }
            }
        }
        // now we can safely remove the artist
        // first we remove the songs from everywhere
        for (Album album: artist.getAlbums()) {
            for (Song song: album.getSongs()) {
                for (User user: library.getUsers()) {
                    user.getLikedSongs().remove(song);
                    for (int i = 0; i < user.getPlaylists().size(); i++) {
                        user.getPlaylists().get(i).getSongs().remove(song);
                        if (user.getPlaylists().get(i).getSongs().isEmpty()) {
                            for (User user1: library.getUsers()) {
                                if (!user1.equals(user)) {
                                    user1.getFollowedPlaylists().remove(
                                            user.getPlaylists().get(i));
                                }
                            }
                            user.getPlaylists().remove(i);
                            i--;
                        }
                    }
                }
                library.getSongs().remove(song);
            }
        }
        library.getArtists().remove(artist);
        return true;
    }

    /**
     * The deleting process of a host
     * @return
     */
    private boolean deleteForHost() {
        Host host = library.getHost(username);
        // we check if any player plays any of the podcasts that
        // this host holds
        for (Player player: players) {
            if (!player.getName().isEmpty()
                && player.getType().equals("podcast")) {
                if (host.checkIfPodcastExists(player.getName(),
                        player.getArtist())) {
                    return false;
                }
            }
        }
        // now we check if any normal user is on the page
        // of this host
        for (User user: library.getUsers()) {
            TypeVisitor visitor = new TypeVisitor();
            if (user.getPage() != null && user.accept(visitor).equals("host")) {
                Host castedHost = (Host) user.getPage();
                if (castedHost.equals(host)) {
                    return false;
                }
            }
        }
        // if we are here we can delete our host
        for (Podcast podcast: host.getPodcasts()) {
            int indexLibrary = 0;
            for (int i = 0; i < library.getPodcasts().size(); i++) {
                if (library.getPodcasts().get(i).equals(podcast)) {
                    indexLibrary = i;
                    break;
                }
            }
            for (Player player: players) {
                player.getSituationPodcasts().remove(indexLibrary);
            }
            library.getPodcasts().remove(podcast);
        }
        library.getHosts().remove(host);
        return true;
    }

    /**
     * Starting point for deleting
     * @return
     */
    private boolean startDelete() {
        switch (library.typeOfUser(username)) {
            case 1 -> {
                return this.deleteForNormalUser();
            }
            case 2 -> {
                return this.deleteForHost();
            }
            case THREE -> {
                return this.deleteForArtist();
            }
            default -> { }
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
    public void startCommand(final ObjectMapper objectMapper,
                             final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        // if not a valid user we can't continue
        if (library.typeOfUser(username) == 0) {
            node.put("message", "The username " + username + " doesn't exist.");
            outputs.add(node);
            return;
        }
        // we update all players
        for (Player player: players) {
            User user = library.getUser(player.getUsername());
            if (!player.getName().isEmpty() && !player.isPaused()
                    && user.isStatus()) {
                this.updatePlayer(player, timestamp);
            }
        }
        // proceed with the deletion if possible
        if (this.startDelete()) {
            node.put("message", username + " was successfully deleted.");
            outputs.add(node);
            return;
        }
        node.put("message", username + " can't be deleted.");
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
