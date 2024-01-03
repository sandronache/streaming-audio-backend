package application.commands.statistics;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.users.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static application.constants.Constants.FIVE;

/**
 * Class for GetTop5Playlists command;
 */
@Getter
public final class GetTop5Playlists implements Commands {
    private String command;
    private Integer timestamp;
    private Library library;

    /**
     * Constructor
     * @param command
     * @param timestamp
     */
    public GetTop5Playlists(final String command, final Integer timestamp,
                            final Library library) {
        this.command = command;
        this.timestamp = timestamp;
        this.library = library;
    }

    /**
     * Creates a list with all public playlists
     * @return
     */
    private ArrayList<Playlist> getNotSortedArray() {
        ArrayList<Playlist> sortedArray = new ArrayList<>();
        for (User user : library.getUsers()) {
            if (user.getPlaylists().isEmpty()) {
                continue;
            }
            for (Playlist playlist : user.getPlaylists()) {
                if (playlist.isVisibility()) {
                    sortedArray.add(playlist);
                }
            }
        }
        return sortedArray;
    }

    /**
     * Sortes the list of public playlists ascending by the followers number
     * and the order they were created
     * @return
     */
    private ArrayList<Playlist> getSortedArray() {
        ArrayList<Playlist> sortedArray = this.getNotSortedArray();
        if (sortedArray.isEmpty()) {
            return sortedArray;
        }
        Comparator<Playlist> playlistComparator = Comparator
                .comparing(Playlist::getFollowers, Comparator.reverseOrder())
                .thenComparing(Playlist::getGlobalPlaylistId);

        Collections.sort(sortedArray, playlistComparator);
        return sortedArray;
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

        ArrayList<Playlist> sortedArray = this.getSortedArray();

        if (sortedArray.isEmpty()) {
            outputs.add(node);
            return;
        }

        ArrayList<String> printList = new ArrayList<>();
        for (int i = 0; i < sortedArray.size() && i < FIVE; i++) {
            printList.add(sortedArray.get(i).getName());
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

}
