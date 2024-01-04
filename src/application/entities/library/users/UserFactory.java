package application.entities.library.users;

import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import application.entities.library.users.normal.User;

/**
 * Factory that creates a new User after the type given
 * by the addUser command
 */
 public final class UserFactory {
    /**
     * Method for creation
     * @param type
     * @return
     */
    public UserDatabase createUser(final String type) {
        if ("user".equalsIgnoreCase(type)) {
            return new User();
        } else if ("artist".equalsIgnoreCase(type)) {
            return new Artist();
        } else if ("host".equalsIgnoreCase(type)) {
            return new Host();
        }
        return null;
    }
}
