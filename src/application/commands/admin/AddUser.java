package application.commands.admin;

import application.commands.root.Commands;
import application.entities.library.Library;
import application.entities.library.users.UserDatabase;
import application.entities.library.users.UserFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

/**
 * Class for addUser command
 */
@Getter
public final class AddUser implements Commands {
    private String command;
    private Integer timestamp;
    private String type;
    private Library library;
    private String username;
    private int age;
    private String city;

    /**
     * Constructor
     * @param command
     * @param timestamp
     * @param type
     * @param library
     * @param username
     * @param age
     * @param city
     */
    public AddUser(final String command, final Integer timestamp, final String type,
                   final Library library, final String username,
                   final int age, final String city) {
        this.command = command;
        this.timestamp = timestamp;
        this.type = type;
        this.library = library;
        this.username = username;
        this.age = age;
        this.city = city;
    }

    /**
     * Method that tries to add the new User and prints the result
     * @param objectMapper
     * @param outputs
     */
    @Override
    public void startCommand(final ObjectMapper objectMapper, final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());

        if (library.typeOfUser(username) != 0) {
            node.put("message", "The username " + username + " is already taken.");
            outputs.add(node);
            return;
        }
        // make a new factory
        UserFactory factory = new UserFactory();
        // create the user depending on type
        UserDatabase newUser = factory.createUser(type);
        // if the artist is new we create an account for him
        if (this.type.equals("artist")) {
            library.checkIfExistsAccount(this.username);
        }
        // put fields and add to the library
        newUser.build(username, age, city, library);

        node.put("message", "The username " + username + " has been added successfully.");
        outputs.add(node);
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public void setTimestamp(final Integer timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public void setCity(final String city) {
        this.city = city;
    }
}
