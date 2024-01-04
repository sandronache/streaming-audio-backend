package application.commands.statistics;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.normal.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for getOnlineUsers command
 */
@Getter
public final class GetOnlineUsers implements Commands {
    private String command;
    private Integer timestamp;
    private Library library;

    public GetOnlineUsers(final String command, final Integer timestamp,
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
    public void startCommand(final ObjectMapper objectMapper, final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("timestamp", this.getTimestamp());
        // we add all normal users usernames in a list
        ArrayList<String> list =  new ArrayList<>();
        for (User user: library.getUsers()) {
            if (user.isStatus()) {
                list.add(user.getUsername());
            }
        }
        // and print it
        ArrayNode arrayNode;
        if (!list.isEmpty()) {
            arrayNode = objectMapper.valueToTree(list);
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
}
