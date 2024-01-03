package application.entities.pages.visitor;

import application.entities.library.Episode;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.Podcast;
import application.entities.library.users.User;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.artist.Event;
import application.entities.library.users.artist.Merch;
import application.entities.library.users.host.Announcement;
import application.entities.library.users.host.Host;
import application.entities.pages.HomePage;
import application.entities.pages.LikedContentPage;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import static application.constants.Constants.FIVE;
import static application.constants.Constants.FOUR;

/**
 * Visitor for printing the current page for a user
 */
@Getter
public final class DisplayVisitor implements PageVisitor {
    private ObjectNode node;
    private Library library;

    /**
     * Constructor
     * @param node - where to put the certain output
     * @param library
     */
    public DisplayVisitor(final ObjectNode node,
                          final Library library) {
        this.node = node;
        this.library = library;
    }

    /**
     * Method that determines the owner of a playlist
     * @param playlistParam
     * @return the name of the owner or nothing ("")
     */
    public String getOwnerPlaylist(final Playlist playlistParam) {
        for (User user: library.getUsers()) {
            for (Playlist playlist: user.getPlaylists()) {
                if (playlist.equals(playlistParam)) {
                    return user.getUsername();
                }
            }
        }
        return "";
    }

    /**
     * Prints the liked content page with
     * the desired format
     * @param page
     */
    @Override
    public void visit(final LikedContentPage page) {
        String string = "Liked songs:\n\t[";
        for (int i = 0; i < page.getLikedSongs().size(); i++) {
            string += page.getLikedSongs().get(i).getName() + " - "
                        + page.getLikedSongs().get(i).getArtist();
            if (i != (page.getLikedSongs().size() - 1)) {
                string += ", ";
            }
        }
        string += "]\n\nFollowed playlists:\n\t[";
        for (int i = 0; i < page.getFollowedPlaylists().size(); i++) {
            string += page.getFollowedPlaylists().get(i).getName() + " - "
                    + this.getOwnerPlaylist(page.getFollowedPlaylists().get(i));
            if (i != (page.getFollowedPlaylists().size() - 1)) {
                string += ", ";
            }
        }
        string += "]";
        node.put("message", string);
    }

    /**
     * Prints artist page
     * @param page
     */
    @Override
    public void visit(final Artist page) {
        String string = "Albums:\n\t[";
        for (int i = 0; i < page.getAlbums().size(); i++) {
            string += page.getAlbums().get(i).getName();
            if (i != (page.getAlbums().size() - 1)) {
                string += ", ";
            }
        }
        string += "]\n\nMerch:\n\t[";
        for (int i = 0; i < page.getMerchandise().size(); i++) {
            Merch merch = page.getMerchandise().get(i);
            string += merch.getName() + " - " +  merch.getPrice() + ":\n\t"
                    + merch.getDescription();
            if (i != (page.getMerchandise().size() - 1)) {
                string += ", ";
            }
        }
        string += "]\n\nEvents:\n\t[";
        for (int i = 0; i < page.getEvents().size(); i++) {
            Event event = page.getEvents().get(i);
            string += event.getName() + " - " +  event.getDate() + ":\n\t"
                    + event.getDescription();
            if (i != (page.getEvents().size() - 1)) {
                string += ", ";
            }
        }
        string += "]";
        node.put("message", string);
    }

    /**
     * Prints host page
     * @param page
     */
    @Override
    public void visit(final Host page) {
        String string = "Podcasts:\n\t[";
        for (int i = 0; i < page.getPodcasts().size(); i++) {
            Podcast podcast = page.getPodcasts().get(i);
            string += podcast.getName() + ":\n\t[";
            for (int j = 0; j < podcast.getEpisodes().size(); j++) {
                Episode episode = podcast.getEpisodes().get(j);
                string += episode.getName() + " - " + episode.getDescription();
                if (j != (podcast.getEpisodes().size() - 1)) {
                    string += ", ";
                }
            }
            string += "]\n";
            if (i != (page.getPodcasts().size() - 1)) {
                string += ", ";
            }
        }
        string += "]\n\nAnnouncements:\n\t[";
        for (int i = 0; i < page.getAnnouncements().size(); i++) {
            Announcement announcement = page.getAnnouncements().get(i);
            string += announcement.getName() + ":\n\t"
                    + announcement.getDescription() + "\n";
            if (i != (page.getAnnouncements().size() - 1)) {
                string += ", ";
            }
        }
        string += "]";
        node.put("message", string);
    }

    /**
     * Prints the home page
     * @param page
     */
    @Override
    public void visit(final HomePage page) {
        String string = "Liked songs:\n\t[";
        for (int i = 0; i < page.getSongs().size() && i < FIVE; i++) {
            string += page.getSongs().get(i).getName();
            if (i != (page.getSongs().size() - 1)
                    && i != FOUR) {
                string += ", ";
            }
        }
        string += "]\n\nFollowed playlists:\n\t[";
        for (int i = 0; i < page.getPlaylists().size() && i < FIVE; i++) {
            string += page.getPlaylists().get(i).getName();
            if (i != (page.getPlaylists().size() - 1)
                    && i != FOUR) {
                string += ", ";
            }
        }
        string += "]";
        node.put("message", string);
    }

    public void setNode(final ObjectNode node) {
        this.node = node;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }
}
