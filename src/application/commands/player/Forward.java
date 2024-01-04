package application.commands.player;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Episode;
import application.entities.library.Library;
import application.entities.library.Podcast;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

import static application.constants.Constants.NINETY;

/**
 * Class for Forward command
 */
@Getter
public final class Forward extends PlayerRelatedCommands {
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
    public Forward(final String command, final String username,
                   final Integer timestamp, final Library library,
                   final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.players = players;
    }

    /**
     * Forward command
     */
    public void forwardAction(final Player currentPlayer) {
        int i = currentPlayer.podcastIndex(true);
        int j = currentPlayer.podcastIndex(false);
        Podcast podcast = currentPlayer.getLibrary().getPodcasts().get(i);
        Episode episode = currentPlayer.getLibrary().getPodcasts().get(i).getEpisodes().get(j);
        if (currentPlayer.getSituationPodcasts().get(i).getNameEpisode().equals(
                episode.getName())) {
            if (NINETY + currentPlayer.getSituationPodcasts().get(i).getMinute()
                    <= episode.getDuration()) {
                currentPlayer.getSituationPodcasts().get(i).setMinute(
                        NINETY + currentPlayer.getSituationPodcasts().get(i).getMinute());
            } else {
                if (j == (podcast.getEpisodes().size() - 1)) {
                    currentPlayer.getSituationPodcasts().get(i).setMinute(0);
                    currentPlayer.getSituationPodcasts().get(i).setNameEpisode(
                            podcast.getEpisodes().get(0).getName());
                    // add to the wrapped
                    library.addEpisodeForUserAndHost(this.username,
                            podcast.getEpisodes().get(0), podcast.getOwner());
                } else {
                    currentPlayer.getSituationPodcasts().get(i).setMinute(0);
                    currentPlayer.getSituationPodcasts().get(i).setNameEpisode(
                            podcast.getEpisodes().get(j + 1).getName());
                    // add to the wrapped
                    library.addEpisodeForUserAndHost(this.username,
                            podcast.getEpisodes().get(j + 1), podcast.getOwner());
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

        Player currentPlayer = this.getCurrentPlayer(players,
                this.getUsername());
        if (currentPlayer == null || currentPlayer.getName().isEmpty()) {
            node.put("message",
                    "Please load a source before attempting to forward.");
            outputs.add(node);
            return;
        }

        if (!currentPlayer.isPaused()) {
            this.updatePlayer(currentPlayer, this.timestamp);
        }

        if (currentPlayer.getName().isEmpty()) {
            node.put("message",
                    "Please load a source before attempting to forward.");
            outputs.add(node);
            return;
        }

        if (!currentPlayer.getType().equals("podcast")) {
            node.put("message",
                    "The loaded source is not a podcast.");
            outputs.add(node);
            return;
        }

        this.forwardAction(currentPlayer);

        node.put("message", "Skipped forward successfully.");
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
