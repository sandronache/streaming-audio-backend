package application.entities.library.users;

import application.entities.library.Library;

/**
 * Interface that represents all users in the application
 */
public interface UserDatabase {
    /**
     * Method that build a user
     * @param username
     * @param age
     * @param city
     * @param library
     */
    void build(String username, int age, String city, Library library);
}
