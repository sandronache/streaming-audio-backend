package application.entities.pages.managervisitor;

import application.entities.library.Playlist;
import application.entities.library.Song;
import application.entities.library.users.normal.User;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import application.entities.pages.HomePage;
import application.entities.pages.LikedContentPage;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Visitor for updating the page
 */
@Getter
public final class UpdateVisitor implements PageVisitor {
    private User user;

    /**
     * Constructor
     * @param user
     */
    public UpdateVisitor(final User user) {
        this.user = user;
    }

    /**
     * Liked page contains exactly the liked songs and the followed
     * playlists of the user calling the visitor
     * @param page
     */
    @Override
    public void visit(final LikedContentPage page) {
            page.setLikedSongs(user.getLikedSongs());
            page.setFollowedPlaylists(user.getFollowedPlaylists());
    }

    /**
     * For the artist, artist being a reference,
     * the page being that exact artist is always
     * updated
     * @param page
     */
    @Override
    public void visit(final Artist page) {
    }

    /**
     * The same with the Host
     * @param page
     */
    @Override
    public void visit(final Host page) {
    }

    /**
     * Method that calculates the total likes in a playlist
     * @param playlist
     * @return
     */
    private Integer totalLikesPlaylist(final Playlist playlist) {
        Integer result = 0;
        for (Song song: playlist.getSongs()) {
            result += song.getLikes();
        }
        return result;
    }

    /**
     * Builds the home page
     * @param page
     */
    @Override
    public void visit(final HomePage page) {
        if (!user.getLikedSongs().isEmpty()) {
            // sort the liked songs
            ArrayList<Song> likedUserSongs = new ArrayList<>(user.getLikedSongs());
            Collections.sort(likedUserSongs, Comparator.comparing(Song::getLikes,
                    Collections.reverseOrder()));
            page.setSongs(likedUserSongs);
        } else {
            page.setSongs(user.getLikedSongs());
        }

        if (!user.getFollowedPlaylists().isEmpty()) {
            ArrayList<Playlist> followedUserPlaylists = new ArrayList<>(
                    user.getFollowedPlaylists());
            // we calculate the likes per playlist
            ArrayList<Integer> likesPerPlaylist =  new ArrayList<>();
            for (Playlist playlist:  followedUserPlaylists) {
                likesPerPlaylist.add(this.totalLikesPlaylist(playlist));
            }
            // an indices array
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < followedUserPlaylists.size(); i++) {
                indices.add(i);
            }
            // we sort the indices after likes
            Collections.sort(indices, Comparator.comparingInt(likesPerPlaylist::get).reversed());
            // we use the sorted indices to sort the playlists
            ArrayList<Playlist> sortedPlaylists = new ArrayList<>();
            for (int index : indices) {
                sortedPlaylists.add(followedUserPlaylists.get(index));
            }
            page.setPlaylists(sortedPlaylists);
        } else {
            page.setPlaylists(user.getFollowedPlaylists());
        }
        // move the recommendedSongs
        if (!user.getRecommendedSongs().isEmpty()) {
            ArrayList<Song> tempRecSongs = new ArrayList<>(user.getRecommendedSongs());
            page.setRecommendedSongs(tempRecSongs);
        }
        // move the recommendedPlaylists
        if (!user.getRecommendedPlaylists().isEmpty()) {
            ArrayList<Playlist> tempRecPlaylists = new ArrayList<>(user.getRecommendedPlaylists());
            page.setRecommendedPlaylists(tempRecPlaylists);
        }
    }

    public void setUser(final User user) {
        this.user = user;
    }

}
