package application.commands.statistics;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static application.constants.Constants.FIVE;

/**
 * Class for GetTop5Songs command;
 */
@Getter
public final class GetTop5Songs implements Commands {
    private String command;
    private Integer timestamp;
    private Library library;

    /**
     * Constructor
     * @param command
     * @param timestamp
     * @param library - the library containing songs, podcasts and users
     */
    public GetTop5Songs(final String command, final Integer timestamp,
                        final Library library) {
        this.command = command;
        this.timestamp = timestamp;
        this.library = library;
    }

    /**
     * Calculates the likes per song
     * @return - a list with number of total likes for each song
     */
    private List<Integer> getLikesPerSong() {
        List<Integer> likesPerSong = new ArrayList<>();
        for (Song song: library.getSongs()) {
            likesPerSong.add(song.getLikes());
        }
        return likesPerSong;
    }

    /**
     * Makes a copy of the indices before sorting
     * @param likesPerSong - list of total likes per each song
     * @return - list of indices
     */
    private List<Integer> getIndicesLikes(final List<Integer> likesPerSong) {
        List<Integer> indicesLikes = new ArrayList<>();
        for (int i = 0; i < likesPerSong.size(); i++) {
            indicesLikes.add(i);
        }
        return indicesLikes;
    }

    /**
     * Sorts indices and likes list
     * @param likesPerSong -  likes list
     * @param indicesLikes - indices list
     */
    private void sortLists(final List<Integer> likesPerSong,
                            final List<Integer> indicesLikes) {
        Comparator<Integer> likesComparator = Comparator.comparingInt(likesPerSong::get).reversed();
        indicesLikes.sort(likesComparator);

        Comparator<Integer> descendingComparator = Comparator.reverseOrder();
        likesPerSong.sort(descendingComparator);
    }

    /**
     * Gets the sorted list of songs, descending
     * @param indicesLikes - sorted indices
     * @return - list of songs ordered descending
     */
    private List<Song> getSortedList(final List<Integer> indicesLikes) {
        List<Song> sortedSongs = new ArrayList<>();
        for (int index : indicesLikes) {
            sortedSongs.add(library.getSongs().get(index));
        }
        return sortedSongs;
    }

    /**
     * Prints and applies the command
     * @param objectMapper
     * @param outputs
     */
    public void startCommand(final ObjectMapper objectMapper, final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("timestamp", this.getTimestamp());

        List<Integer> likesPerSong = getLikesPerSong();
        List<Integer> indicesLikes = getIndicesLikes(likesPerSong);

        // we sort after likes
        this.sortLists(likesPerSong, indicesLikes);

        List<Song> sortedSongs = getSortedList(indicesLikes);

        ArrayList<String> printList = new ArrayList<>();
        for (int i = 0; i < FIVE; i++) {
            printList.add(sortedSongs.get(i).getName());
        }

        ArrayNode arrayNode = objectMapper.valueToTree(printList);
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
