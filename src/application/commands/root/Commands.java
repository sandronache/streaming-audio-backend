package application.commands.root;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Interface for commands
 */
public interface Commands {
    /**
     * Method applies the command and prints
     * @param objectMapper
     * @param outputs
     */
    void startCommand(ObjectMapper objectMapper, ArrayNode outputs);
}
