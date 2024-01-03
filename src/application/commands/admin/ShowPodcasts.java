package application.commands.admin;

import application.commands.root.Commands;
import application.entities.library.Episode;
import application.entities.library.Library;
import application.entities.library.Podcast;
import application.entities.library.users.host.Host;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for showPodcasts command
 */
@Getter
public final class ShowPodcasts implements Commands {
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
    public ShowPodcasts(final String command, final String username,
                      final Integer timestamp, final Library library) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
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

        // get the host
        Host host = library.getHost(username);
        ArrayNode podcastsArray =  objectMapper.createArrayNode();
        for (Podcast podcast: host.getPodcasts()) {
            // add each podcast
            ObjectNode node1 = objectMapper.createObjectNode();
            node1.put("name", podcast.getName());

            ArrayList<String> nameOfEpisodes = new ArrayList<>();
            for (Episode episode: podcast.getEpisodes()) {
                nameOfEpisodes.add(episode.getName());
            }

            ArrayNode arrayNode;
            if (!nameOfEpisodes.isEmpty()) {
                arrayNode = objectMapper.valueToTree(nameOfEpisodes);
            } else {
                arrayNode = objectMapper.createArrayNode();
            }
            node1.set("episodes", arrayNode);

            podcastsArray.add(node1);
        }
        // print all
        node.set("result", podcastsArray);
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
}
