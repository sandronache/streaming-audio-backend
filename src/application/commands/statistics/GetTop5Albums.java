package application.commands.statistics;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.artist.Album;
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
 * Class for getTop5Albums command
 */
@Getter
public final class GetTop5Albums implements Commands {
    private String command;
    private Integer timestamp;
    private Library library;

    public GetTop5Albums(final String command, final Integer timestamp,
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

        // we get all albums
        ArrayList<Album> allAlbums = new ArrayList<>();
        for (Artist artist: library.getArtists()) {
            allAlbums.addAll(artist.getAlbums());
        }
        // we calculate total likes for each album
        ArrayList<Integer> likesPerAlbum = new ArrayList<>();
        for (Album album: allAlbums) {
            likesPerAlbum.add(album.likesPerAlbum());
        }
        // we get an indices array
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < likesPerAlbum.size(); i++) {
            indices.add(i);
        }

        Comparator<Integer> comparator = (index1, index2) -> {
            int likesComparison = Integer.compare(likesPerAlbum.get(index2),
                    likesPerAlbum.get(index1));
            if (likesComparison == 0) {
                return allAlbums.get(index1).getName().compareTo(allAlbums.get(index2).getName());
            }
            return likesComparison;
        };
        // we sort the indices after the total likes
        Collections.sort(indices, comparator);
        ArrayList<String> sortedAlbums = new ArrayList<>();
        for (int index : indices) {
            sortedAlbums.add(allAlbums.get(index).getName());
            if (sortedAlbums.size() == FIVE) {
                break;
            }
        }
        // we print the albums
        ArrayNode arrayNode;
        if (!sortedAlbums.isEmpty()) {
            arrayNode = objectMapper.valueToTree(sortedAlbums);
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
