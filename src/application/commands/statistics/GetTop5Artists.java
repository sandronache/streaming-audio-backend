package application.commands.statistics;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.artist.Artist;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static application.constants.Constants.FIVE;

/**
 * Class for getTop5Artists
 */
@Getter
public final class GetTop5Artists implements Commands {
    private String command;
    private Integer timestamp;
    private Library library;

    public GetTop5Artists(final String command, final Integer timestamp,
                         final Library library) {
        this.command = command;
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
        node.put("timestamp", this.getTimestamp());

        // we get all artists
        ArrayList<Artist> allArtist = new ArrayList<>(library.getArtists());
        ArrayList<Integer> likesPerArtist = new ArrayList<>();
        // we calculate the total of likes for all artists
        for (Artist artist: allArtist) {
            likesPerArtist.add(artist.likesPerArtist());
        }

        // create an indices array
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < likesPerArtist.size(); i++) {
            indices.add(i);
        }

        Comparator<Integer> comparator = (index1, index2) -> {
            int likesComparison = Integer.compare(
                    likesPerArtist.get(index2), likesPerArtist.get(index1));
            if (likesComparison == 0) {
                return allArtist.get(index1).getUsername().compareTo(
                        allArtist.get(index2).getUsername());
            }
            return likesComparison;
        };

        // we sort the indices after the total likes
        Collections.sort(indices, comparator);
        ArrayList<String> sortedArtists = new ArrayList<>();
        for (int index : indices) {
            sortedArtists.add(allArtist.get(index).getUsername());
            if (sortedArtists.size() == FIVE) {
                break;
            }
        }

        // print the artists
        ArrayNode arrayNode;
        if (!sortedArtists.isEmpty()) {
            arrayNode = objectMapper.valueToTree(sortedArtists);
        } else {
            arrayNode = objectMapper.createArrayNode();
        }
        node.set("result", arrayNode);
        outputs.add(node);
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public void setTimestamp(final Integer timestamp) {
        this.timestamp = timestamp;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

}
