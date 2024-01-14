package application.commands.root;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * This class represents the invoker that starts a command
 */
public final class Invoker {
    private Commands command;
    private ObjectMapper objectMapper;
    private ArrayNode outputs;

    /**
     * Constructor
     * @param command
     * @param objectMapper
     * @param outputs
     */
    public Invoker(final Commands command, final ObjectMapper objectMapper,
                   final ArrayNode outputs) {
        this.command = command;
        this.objectMapper = objectMapper;
        this.outputs = outputs;
    }

    /**
     * With this method the invoker starts the action
     */
    void action() {
        command.startCommand(objectMapper, outputs);
    }
}
