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

/**
 * Class for Next command
 */
@Getter
public final class Next extends PlayerRelatedCommands {
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
    public Next(final String command, final String username,
                final Integer timestamp, final Library library,
                final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.players = players;
    }

    /**
     * Next command for song
     */
    public void nextForSong(final Player currentPlayer) {
        if (currentPlayer.getRepeat() == 0) {
            currentPlayer.setNewStatusSong(currentPlayer.getSong(), "",
                    "song", 0, timestamp, 0,
                    false, true);
            return;
        }
        if (currentPlayer.getRepeat() == 1) {
            currentPlayer.setRepeat(0);
        }
        currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
        currentPlayer.setPaused(false);
    }

    /**
     * Next command for playlist not on shuffle
     * @param songIndex = index current melody in player
     */
    public void nextForPlaylistRepeat(final Player currentPlayer, final int songIndex) {
        int index = songIndex;
        if (index == (currentPlayer.getPlaylist().getSongs().size() - 1)) {
            if (currentPlayer.getRepeat() == 0) {
                currentPlayer.setNewStatusPlaylist(currentPlayer.getSong(), "",
                        "playlist", currentPlayer.getPlaylist(), 0,
                        timestamp, 0, false, true);
                return;
            } else {
                index = 0;
                currentPlayer.setSong(currentPlayer.getPlaylist().getSongs().get(index));
                currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
            }
            currentPlayer.setPaused(false);
            return;
        }
        index = index + 1;
        currentPlayer.setSong(currentPlayer.getPlaylist().getSongs().get(index));
        currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
        currentPlayer.setPaused(false);
    }

    /**
     * Next command for playlist on shuffle
     * @param songIndex = index current melody in player
     */
    public void nextForPlaylistRepeatShuffle(final Player currentPlayer, final int songIndex) {
        int index = songIndex;
        int songIndexShuffle = currentPlayer.getIndexFromShuffle(index);
        if (songIndexShuffle == (currentPlayer.getIndicesShuffle().size() - 1)) {
            if (currentPlayer.getRepeat() == 0) {
                currentPlayer.setNewStatusPlaylist(currentPlayer.getSong(), "",
                        "playlist", currentPlayer.getPlaylist(), 0,
                        timestamp, 0, false, true);
                return;
            } else {
                songIndexShuffle = 0;
                index = currentPlayer.getIndicesShuffle().get(songIndexShuffle);
                currentPlayer.setSong(currentPlayer.getPlaylist().getSongs().get(index));
                currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
            }
            currentPlayer.setPaused(false);
            return;
        }
        songIndexShuffle = songIndexShuffle + 1;
        index = currentPlayer.getIndicesShuffle().get(songIndexShuffle);
        currentPlayer.setSong(currentPlayer.getPlaylist().getSongs().get(index));
        currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
        currentPlayer.setPaused(false);
    }

    /**
     * Next for podcast
     */
    public void nextForPodcast(final Player currentPlayer) {
        int i = currentPlayer.podcastIndex(true);
        int j = currentPlayer.podcastIndex(false);
        Podcast podcast = currentPlayer.getLibrary().getPodcasts().get(i);
        if (currentPlayer.getRepeat() == 0) {
            if (j == (podcast.getEpisodes().size() - 1)) {
                currentPlayer.setNewStatusPodcast("", "",
                        "podcast", timestamp, 0,
                        false, true);
                currentPlayer.getSituationPodcasts().get(i).setMinute(0);
                currentPlayer.getSituationPodcasts().get(i).setNameEpisode(
                        podcast.getEpisodes().get(0).getName());
                return;
            }
            currentPlayer.getSituationPodcasts().get(i).setNameEpisode(
                    podcast.getEpisodes().get(j + 1).getName());
        } else if (currentPlayer.getRepeat() == 1) {
            currentPlayer.setRepeat(0);
        }
        currentPlayer.getSituationPodcasts().get(i).setMinute(0);
        currentPlayer.setPaused(false);
    }

    /**
     * Next command for album not on shuffle
     * @param songIndex = index current melody in player
     */
    public void nextForAlbumRepeat(final Player currentPlayer, final int songIndex) {
        int index = songIndex;
        if (index == (currentPlayer.getAlbum().getSongs().size() - 1)) {
            if (currentPlayer.getRepeat() == 0) {
                currentPlayer.setNewStatusAlbum(currentPlayer.getSong(), "",
                        "album", currentPlayer.getAlbum(), 0,
                        timestamp, 0, false, true);
                return;
            } else {
                index = 0;
                currentPlayer.setSong(currentPlayer.getAlbum().getSongs().get(index));
                currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
            }
            currentPlayer.setPaused(false);
            return;
        }
        index = index + 1;
        currentPlayer.setSong(currentPlayer.getAlbum().getSongs().get(index));
        currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
        currentPlayer.setPaused(false);
    }

    /**
     * Next command for album on shuffle
     * @param songIndex = index current melody in player
     */
    public void nextForAlbumRepeatShuffle(final Player currentPlayer, final int songIndex) {
        int index = songIndex;
        int songIndexShuffle = currentPlayer.getIndexFromShuffle(index);
        if (songIndexShuffle == (currentPlayer.getIndicesShuffle().size() - 1)) {
            if (currentPlayer.getRepeat() == 0) {
                currentPlayer.setNewStatusAlbum(currentPlayer.getSong(), "",
                        "album", currentPlayer.getAlbum(), 0,
                        timestamp, 0, false, true);
                return;
            } else {
                songIndexShuffle = 0;
                index = currentPlayer.getIndicesShuffle().get(songIndexShuffle);
                currentPlayer.setSong(currentPlayer.getAlbum().getSongs().get(index));
                currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
            }
            currentPlayer.setPaused(false);
            return;
        }
        songIndexShuffle = songIndexShuffle + 1;
        index = currentPlayer.getIndicesShuffle().get(songIndexShuffle);
        currentPlayer.setSong(currentPlayer.getAlbum().getSongs().get(index));
        currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
        currentPlayer.setPaused(false);
    }

    /**
     * Next starting point
     */
    public void nextCommand(final Player currentPlayer) {
        switch (currentPlayer.getType()) {
            case "song" -> this.nextForSong(currentPlayer);
            case "playlist" -> {
                if (currentPlayer.getRepeat() == 2) {
                    currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
                    currentPlayer.setPaused(false);
                } else {
                    int songIndex = currentPlayer.playlistIndex();
                    if (currentPlayer.isShuffle()) {
                        this.nextForPlaylistRepeatShuffle(currentPlayer, songIndex);
                    } else {
                        this.nextForPlaylistRepeat(currentPlayer, songIndex);
                    }
                }
            }
            case "podcast" -> this.nextForPodcast(currentPlayer);
            default -> {
                if (currentPlayer.getRepeat() == 2) {
                    currentPlayer.setRemainedTime(currentPlayer.getSong().getDuration());
                } else {
                    int songIndex = currentPlayer.albumIndex();
                    if (currentPlayer.isShuffle()) {
                        this.nextForAlbumRepeatShuffle(currentPlayer, songIndex);
                    } else {
                        this.nextForAlbumRepeat(currentPlayer, songIndex);
                    }
                }
            }
        }
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
                    "Please load a source before skipping to the next track.");
            outputs.add(node);
            return;
        }
        if (!currentPlayer.isPaused()) {
            this.updatePlayer(currentPlayer, this.timestamp);
        }

        currentPlayer.setTimestamp(timestamp);
        this.nextCommand(currentPlayer);

        if (currentPlayer.getName().isEmpty()) {
            node.put("message",
                    "Please load a source before skipping to the next track.");
        } else {
            String str = "Skipped to next track successfully. The current track is ";
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
