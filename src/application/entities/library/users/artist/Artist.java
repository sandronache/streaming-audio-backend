package application.entities.library.users.artist;

import application.entities.library.Library;
import application.entities.library.Song;
import application.entities.library.users.UserDatabase;
import application.entities.library.users.normal.User;
import application.entities.pages.Page;
import application.entities.pages.managervisitor.PageVisitor;
import application.entities.pages.typevisitor.TypePageVisitor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Class that represent an Artist
 */
@Getter
public final class Artist implements UserDatabase, Page {
    private String username;
    private int age;
    private String city;
    private ArrayList<Album> albums;
    private ArrayList<Event> events;
    private ArrayList<Merch> merchandise;
    private ArrayList<User> subscribers;

    /**
     * Function that builds an object(Artist)
     * @param usernameParam
     * @param ageParam
     * @param cityParam
     */
    @Override
    public void build(final String usernameParam, final int ageParam,
                      final String cityParam, final Library libraryParam) {
        this.username = usernameParam;
        this.age = ageParam;
        this.city = cityParam;
        this.albums = new ArrayList<>();
        this.events = new ArrayList<>();
        this.merchandise = new ArrayList<>();
        this.subscribers = new ArrayList<>();
        libraryParam.getArtists().add(this);
    }

    /**
     * Method that accepts the visitor PageVisitor
     * @param visitor
     */
    @Override
    public void accept(final PageVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Method that accepts the visitor TypePageVisitor
     * @param visitor
     */
    @Override
    public String accept(final TypePageVisitor visitor) {
        return visitor.visit(this);
    }

    /**
     * Checks if exists an event with the given name
     * @param name
     * @return
     */
    public boolean checkIfEventExists(final String name) {
        for (Event event: events) {
            if (event.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if exists an album with the given name
     * @param name
     * @return
     */
    public boolean checkIfAlbumExists(final String name) {
        for (Album album: albums) {
            if (album.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the album with the given name
     * @param name
     * @return
     */
    public Album getCertainAlbum(final String name) {
        for (Album album: albums) {
            if (album.getName().equals(name)) {
                return album;
            }
        }
        return null;
    }

    /**
     * Checks if the given song exist in any of the
     * albums this artist has
     * @param songParam
     * @return
     */
    public boolean checkIfSongExists(final Song songParam) {
        for (Album album: albums) {
            for (Song song: album.getSongs()) {
                if (song.equals(songParam)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if exists any merchandise with this name
     * @param name
     * @return
     */
    public boolean checkIfMerchExists(final String name) {
        for (Merch merch: merchandise) {
            if (merch.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the event with the given name
     * @param name
     */
    public void removeEvent(final String name) {
        for (Event event: events) {
            if (event.getName().equals(name)) {
                events.remove(event);
                return;
            }
        }
    }

    /**
     * Calculates the total of likes received
     * by this artist
     * @return
     */
    public int likesPerArtist() {
        int result = 0;
        for (Album album: albums) {
            result += album.likesPerAlbum();
        }
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Artist artist = (Artist) o;
        return age == artist.age
                && Objects.equals(username, artist.username)
                && Objects.equals(city, artist.city)
                && Objects.equals(albums, artist.albums)
                && Objects.equals(events, artist.events)
                && Objects.equals(merchandise, artist.merchandise);
    }

    /**
     * This method finds the most profitable song for an artist
     * @return
     */
    public Song findMostProfitableSong() {
        Song result = null;
        double maximum = 0;
        for (Album album: albums) {
            for (Song song: album.getSongs()) {
                if (song.getRevenue() > maximum) {
                    maximum = song.getRevenue();
                    result = song;
                }
            }
        }
        return result;
    }

    /**
     * Method that gets the price for a merch with the name
     * given as parameter
     * @param nameParam
     * @return
     */
    public Integer getPriceMerch(final String nameParam) {
        for (Merch merch: merchandise) {
            if (merch.getName().equals(nameParam)) {
                return merch.getPrice();
            }
        }
        return 0;
    }

    /**
     * This method sends a notification if possible depending on the type given
     * @param type
     */
    public void sendNotificationIfPossible(final Integer type) {
        for (User subscriber : subscribers) {
            if (type == 0) {
                // means an album
                subscriber.addNotification("New Album from " + this.username + ".");
            }
            if (type == 1) {
                // means a merch
                subscriber.addNotification("New Merchandise from " + this.username + ".");
            }
            if (type == 2) {
                // means an event
                subscriber.addNotification("New Event from " + this.username + ".");
            }
        }
    }

    /**
     * Method that returns true if the user subscribed or false if he unsubscribed
     * @param userParam
     * @return
     */
    public boolean unsubscribeOrSubscribeUser(final User userParam) {
        User tempUser = null;
        for (User subscriber: subscribers) {
            if (subscriber.getUsername()
                    .equals(userParam.getUsername())) {
                tempUser = subscriber;
                break;
            }
        }
        if (tempUser == null) {
            subscribers.add(userParam);
            return true;
        } else {
            subscribers.remove(userParam);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, age, city, albums, events, merchandise);
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

    public void setAlbums(final ArrayList<Album> albums) {
        this.albums = albums;
    }

    public void setEvents(final ArrayList<Event> events) {
        this.events = events;
    }

    public void setMerchandise(final ArrayList<Merch> merchandise) {
        this.merchandise = merchandise;
    }
    public void setSubscribers(final ArrayList<User> subscribers) {
        this.subscribers = subscribers;
    }
}
