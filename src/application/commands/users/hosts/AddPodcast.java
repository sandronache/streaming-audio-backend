package application.commands.users.hosts;

import application.commands.root.Commands;
import application.entities.library.Episode;
import application.entities.library.Library;
import application.entities.library.Podcast;
import application.entities.library.users.host.Host;
import application.entities.player.Player;
import application.entities.player.PlayerPodcast;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for addPodcast command
 */
@Getter
public final class AddPodcast implements Commands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private String name;
    private ArrayList<Episode> episodes;
    private ArrayList<Player> players;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param library
     * @param name
     * @param episodes
     * @param players
     */
    public AddPodcast(final String command, final String username,
                      final Integer timestamp, final Library library,
                      final String name, final ArrayList<Episode> episodes,
                      final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.name = name;
        this.episodes = episodes;
        this.players = players;
    }

    /**
     * Checks if in the list of episodes are any duplicated ones
     * @return
     */
    private boolean checkIfEpisodesDuplicated() {
        for (int i = 0; i < episodes.size(); i++) {
            for (int j = 0; j < episodes.size(); j++) {
                if (i != j && episodes.get(i).equals(episodes.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * For my implementation
     * Makes a new element in the situation podcast list for
     * the new podcast
     * @param podcast
     */
    private void updateSituationsPodcastPlayers(final Podcast podcast) {
        for (Player player: players) {
            PlayerPodcast currentPodcast = new PlayerPodcast(
                    podcast.getEpisodes().get(0).getName(), 0);
            player.getSituationPodcasts().add(currentPodcast);
        }
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

        // we check if this username is a host
        if (library.typeOfUser(username) != 2) {
            node.put("message", username + " is not a host.");
            outputs.add(node);
            return;
        }

        Host host = library.getHost(username);

        // we check if this host already has this podcast
        if (host.checkIfPodcastExists(name)) {
            node.put("message", username + " has another podcast with the same name.");
            outputs.add(node);
            return;
        }

        // we check if there are any duplicated episodes
        if (this.checkIfEpisodesDuplicated()) {
            node.put("message", username + " has the same episode in this podcast.");
            outputs.add(node);
            return;
        }

        // if all checks were alright we add the podcast:
        Podcast podcast = new Podcast(name, username, episodes);
        // to the library
        library.getPodcasts().add(podcast);
        // to the podcast
        host.getPodcasts().add(podcast);
        // we also send notifications if we have subscribers
        host.sendNotificationIfPossible(0);
        // in situation podcast from players
        this.updateSituationsPodcastPlayers(podcast);
        node.put("message", username + " has added new podcast successfully.");
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

    public void setName(final String name) {
        this.name = name;
    }

    public void setEpisodes(final ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

}
