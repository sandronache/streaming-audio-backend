package application.commands.player;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Library;
import application.entities.library.Podcast;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class for Prev command
 */
@Getter
public final class Prev extends PlayerRelatedCommands {
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
    public Prev(final String command, final String username,
                 final Integer timestamp, final Library library, final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.players = players;
    }

    /**
     * Prev for song
     */
    public void prevSong(final Player currentPlayer) {
        if (currentPlayer.getRepeat() == 1) {
            currentPlayer.setRepeat(0);
        }
        currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
        // add to the wrapped
        library.addSongForUser(this.username, currentPlayer.getSong());
    }

    /**
     * Prev for podcast
     */
    public void prevPodcast(final Player currentPlayer) {
        int i = currentPlayer.podcastIndex(true);
        int j = currentPlayer.podcastIndex(false);
        Podcast podcast = currentPlayer.getLibrary().getPodcasts().get(i);
        if (currentPlayer.getSituationPodcasts().get(i).getMinute() == 0
                && j > 0 && currentPlayer.getRepeat() == 0) {
            currentPlayer.getSituationPodcasts().get(i).setNameEpisode(
                    podcast.getEpisodes().get(j - 1).getName());
            // add to the wrapped
            library.addEpisodeForUserAndHost(this.username,
                    podcast.getEpisodes().get(j - 1), podcast.getOwner());
        } else {
            // add to the wrapped
            library.addEpisodeForUserAndHost(this.username,
                    podcast.getEpisodes().get(j), podcast.getOwner());
        }
        if (currentPlayer.getRepeat() == 1) {
            currentPlayer.setRepeat(0);
        }
        currentPlayer.getSituationPodcasts().get(i).setMinute(0);
    }

    /**
     * Prev for playlist on shuffle
     * @param songIndex
     */
    public void prevPlaylistShuffle(final Player currentPlayer, final int songIndex) {
        int songIndexShuffle = currentPlayer.getIndexFromShuffle(songIndex);
        if (songIndexShuffle > 0
                && Objects.equals(currentPlayer.getRemainedTime(),
                currentPlayer.getPlaylist().getSongs().get(songIndex).getDuration())) {
            currentPlayer.setSong(currentPlayer.getPlaylist().getSongs().get(
                    currentPlayer.getIndicesShuffle().get(songIndexShuffle - 1)));
        }
        currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
        // add to the wrapped
        library.addSongForUser(this.username, currentPlayer.getSong());
    }

    /**
     * Prev for album not on shuffle
     * @param songIndex
     */
    public void prevAlbum(final Player currentPlayer, final int songIndex) {
        if (songIndex > 0 && Objects.equals(currentPlayer.getRemainedTime(),
                currentPlayer.getAlbum().getSongs().get(songIndex).getDuration())) {
            currentPlayer.setSong(currentPlayer.getAlbum().getSongs().get(songIndex - 1));
        }
        currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
        // add to the wrapped
        library.addSongForUser(this.username, currentPlayer.getSong());
    }

    /**
     * Prev for album on shuffle
     * @param songIndex
     */
    public void prevAlbumShuffle(final Player currentPlayer, final int songIndex) {
        int songIndexShuffle = currentPlayer.getIndexFromShuffle(songIndex);
        if (songIndexShuffle > 0
                && Objects.equals(currentPlayer.getRemainedTime(),
                currentPlayer.getAlbum().getSongs().get(songIndex).getDuration())) {
            currentPlayer.setSong(currentPlayer.getAlbum().getSongs().get(
                    currentPlayer.getIndicesShuffle().get(songIndexShuffle - 1)));
        }
        currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
        // add to the wrapped
        library.addSongForUser(this.username, currentPlayer.getSong());
    }

    /**
     * Prev for playlist not on shuffle
     * @param songIndex
     */
    public void prevPlaylist(final Player currentPlayer, final int songIndex) {
        if (songIndex > 0 && Objects.equals(currentPlayer.getRemainedTime(),
                currentPlayer.getPlaylist().getSongs().get(songIndex).getDuration())) {
            currentPlayer.setSong(currentPlayer.getPlaylist().getSongs().get(songIndex - 1));
        }
        currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
        // add to the wrapped
        library.addSongForUser(this.username, currentPlayer.getSong());
    }

    /**
     * Prev starting point
     */
    public void prevCommand(final Player currentPlayer) {
        if (currentPlayer.getType().equals("song")) {
            this.prevSong(currentPlayer);
        } else if (currentPlayer.getType().equals("podcast")) {
            this.prevPodcast(currentPlayer);
        } else if (currentPlayer.getType().equals("playlist")) {
            int songIndex = currentPlayer.playlistIndex();
            if (currentPlayer.isShuffle()) {
                this.prevPlaylistShuffle(currentPlayer, songIndex);
            } else {
                this.prevPlaylist(currentPlayer, songIndex);
            }
        } else {
            int songIndex = currentPlayer.albumIndex();
            if (currentPlayer.isShuffle()) {
                this.prevAlbumShuffle(currentPlayer, songIndex);
            } else {
                this.prevAlbum(currentPlayer, songIndex);
            }
        }
        currentPlayer.setPaused(false);
    }

    /**
     * Method applies command and prints
     * @param objectMapper
     * @param outputs
     */
    @Override
    public void startCommand(final ObjectMapper objectMapper, final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        if (!library.getUser(username).isStatus()) {
            node.put("message", username + " is offline.");
            outputs.add(node);
            return;
        }

        Player currentPlayer = this.getCurrentPlayer(players, this.getUsername());
        if (currentPlayer == null || currentPlayer.getName().isEmpty()) {
            node.put("message",
                    "Please load a source before returning to the previous track.");
            outputs.add(node);
            return;
        }
        if (!currentPlayer.isPaused()) {
            this.updatePlayer(currentPlayer, this.timestamp);
        }

        currentPlayer.setTimestamp(timestamp);
        this.prevCommand(currentPlayer);

        if (currentPlayer.getName().isEmpty()) {
            node.put("message",
                    "Please load a source before returning to the previous track.");
        } else {
            String str =
                    "Returned to previous track successfully. The current track is ";
            if (currentPlayer.getType().equals("podcast")) {
                str += currentPlayer.getSituationPodcasts().get(
                        currentPlayer.podcastIndex(true)).getNameEpisode();
            } else {
                str += currentPlayer.getSong().getName();
            }
            str += ".";
            node.put("message", str);
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

}
