package application.entities.library.users.host;

import application.entities.library.Library;
import application.entities.library.Podcast;
import application.entities.library.users.normal.User;
import application.entities.library.users.UserDatabase;
import application.entities.pages.Page;
import application.entities.pages.visitor.PageVisitor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Objects;

import static application.constants.Constants.THREE;

/**
 * Class that represents a Host
 */
@Getter
public final class Host implements UserDatabase, Page {
    private String username;
    private int age;
    private String city;
    private ArrayList<Podcast> podcasts;
    private ArrayList<Announcement> announcements;
    private ArrayList<User> listeners;

    /**
     * Function that builds an object(Host)
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
        this.podcasts = new ArrayList<>();
        this.announcements = new ArrayList<>();
        this.listeners = new ArrayList<>();
        libraryParam.getHosts().add(this);
    }

    /**
     * Method that accepts the visitor
     * @param visitor
     */
    @Override
    public void accept(final PageVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Implementation of whichPage method
     * @return - 3 for Host
     */
    @Override
    public int whichPage() {
        return THREE;
    }

    /**
     * Checks if a podcast exists already with the given name
     * @param name
     * @return
     */
    public boolean checkIfPodcastExists(final String name) {
        for (Podcast podcast: podcasts) {
            if (podcast.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the podcast with the given name
     * if it exists
     * @param name
     * @return
     */
    public Podcast getPodcast(final String name) {
        for (Podcast podcast: podcasts) {
            if (podcast.getName().equals(name)) {
                return podcast;
            }
        }
        return null;
    }

    /**
     * Checks if a podcast exists already with the given name
     * and the given owner
     * @param name
     * @param owner
     * @return
     */
    public boolean checkIfPodcastExists(final String name,
                                        final String owner) {
        for (Podcast podcast: podcasts) {
            if (podcast.getName().equals(name)
                && podcast.getOwner().equals(owner)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks is an announcement exists already
     * with the given name
     * @param name
     * @return
     */
    public boolean checkIfAnnouncementExists(final String name) {
        for (Announcement announcement: announcements) {
            if (announcement.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the announcement with the given name
     * @param name
     */
    public void removeAnnouncement(final String name) {
        for (Announcement announcement: announcements) {
            if (announcement.getName().equals(name)) {
                announcements.remove(announcement);
                return;
            }
        }
    }

    /**
     * Adds a new listener
     * @param userParam
     */
    public void addListener(final User userParam) {
        for (User listener: listeners) {
            if (listener.getUsername().equals(userParam.getUsername())) {
                return;
            }
        }
        listeners.add(userParam);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Host host = (Host) o;
        return age == host.age
                && Objects.equals(username, host.username)
                && Objects.equals(city, host.city)
                && Objects.equals(podcasts, host.podcasts)
                && Objects.equals(announcements, host.announcements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, age, city, podcasts, announcements);
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

    public void setPodcasts(final ArrayList<Podcast> podcasts) {
        this.podcasts = podcasts;
    }

    public void setAnnouncements(final ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }

    public void setListeners(final ArrayList<User> listeners) {
        this.listeners = listeners;
    }
}
