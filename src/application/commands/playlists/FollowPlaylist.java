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
 * Class for FollowPlaylist command
 */
@Getter
public final class FollowPlaylist implements Commands {
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
    public FollowPlaylist(final String command, final String username,
                          final Integer timestamp, final Library library) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
    }

    /**
     * Method that applies the command and prints;
     * @param objectMapper
     * @param outputs
     */
    @Override
    public void startCommand(final ObjectMapper objectMapper, final  ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        User user = library.getUser(username);

        if (!library.getUser(username).isStatus()) {
            node.put("message", username + " is offline.");
            outputs.add(node);
            user.setLastSelected(null);
            return;
        }

        if (user.getLastSelected() == null) {
            node.put("message",
                    "Please select a source before following or unfollowing.");
            outputs.add(node);
            user.setLastSelected(null);
            return;
        }
        if (!user.getTypeLastSearch().equals("playlist")) {
            node.put("message",
                    "The selected source is not a playlist.");
            outputs.add(node);
            user.setLastSelected(null);
            return;
        }
        for (User user1: library.getUsers()) {
            for (Playlist playlist : user1.getPlaylists()) {
                if (user.getLastSelected().equals(playlist.getName())) {
                    if (user1.getUsername().equals(this.getUsername())) {
                        node.put("message",
                                "You cannot follow or unfollow your own playlist.");
                        outputs.add(node);
                        user.setLastSelected(null);
                        return;
                    }
                    if (!playlist.isVisibility()) {
                        outputs.add(node);
                        user.setLastSelected(null);
                        return;
                    }
                    if (playlist.getTheFollowers().isEmpty()) {
                        playlist.setFollowers(1);
                        playlist.addFollower(this.username);
                        library.getUser(this.username).addLikedPlaylist(playlist);
                        node.put("message", "Playlist followed successfully.");
                        outputs.add(node);
                        user.setLastSelected(null);
                        return;
                    }
                    int result = 0;
                    for (String follower : playlist.getTheFollowers()) {
                        if (follower.equals(this.getUsername())) {
                            result = 1;
                            break;
                        }
                    }
                    if (result == 1) {
                        playlist.getTheFollowers().removeIf(follower
                                -> follower.equals(this.getUsername()));
                        playlist.setFollowers(playlist.getFollowers() - 1);
                        user.removeLikedPlaylist(playlist);
                        node.put("message",
                                "Playlist unfollowed successfully.");
                    } else {
                        playlist.addFollower(this.username);
                        playlist.setFollowers(playlist.getFollowers() + 1);
                        user.addLikedPlaylist(playlist);
                        node.put("message",
                                "Playlist followed successfully.");
                    }
                    user.setAsideLastSelected(user.getLastSelected());
                    user.setLastSelected(null);
                    outputs.add(node);
                    return;
                }
            }
        }
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
