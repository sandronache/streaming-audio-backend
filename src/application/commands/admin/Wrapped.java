package application.commands.admin;

import application.commands.root.PlayerRelatedCommands;
import application.entities.library.Episode;
import application.entities.library.Library;
import application.entities.library.Podcast;
import application.entities.library.Song;
import application.entities.library.users.normal.User;
import application.entities.library.users.artist.Album;
import application.entities.library.users.artist.Artist;
import application.entities.library.users.host.Host;
import application.entities.player.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static application.constants.Constants.FIVE;
import static application.constants.Constants.THREE;

/**
 * Class for Wrapped command
 */
@Getter
public final class Wrapped extends PlayerRelatedCommands {
    private String command;
    private String username;
    private Integer timestamp;
    private Library library;
    private ArrayList<Player> players;

    /**
     * Constructor
     * @param command
     * @param username
     * @param timestamp
     * @param library
     * @param players
     */
    public Wrapped(final String command, final String username,
                   final Integer timestamp, final Library library,
                   final ArrayList<Player> players) {
        this.command = command;
        this.username = username;
        this.timestamp = timestamp;
        this.library = library;
        this.players = players;
    }

    /**
     * Updates all players to this timestamp
     */
    private void updateAllPlayers() {
        for (Player player: players) {
            User user = library.getUser(player.getUsername());
            if (!player.getName().isEmpty() && !player.isPaused()
                    && user.isStatus()) {
                this.updatePlayer(player, timestamp);
            }
        }
    }

    /**
     * Method for artist
     * @param objectMapper
     * @param node
     */
    public void actionForArtist(final ObjectMapper objectMapper,
                                final ObjectNode node) {
        // we get the artist
        Artist currentArtist = library.getArtist(this.username);
        // new node
        ObjectNode node1 = objectMapper.createObjectNode();
        // we build the array with all albums
        ArrayList<Album> albumsPerArtist = new ArrayList<>(currentArtist.getAlbums());
        // we sort the albums
        Collections.sort(albumsPerArtist, Comparator
                .<Album, Integer>comparing(Album::getListens).reversed()
                .thenComparing(Album::getName));
        // we build the array with all songs
        ArrayList<Song> songsPerArtist = new ArrayList<>();
        for (Album oneAlbum: currentArtist.getAlbums()) {
            for (Song songEachAlbum: oneAlbum.getSongs()) {
                Song newSong = new Song(songEachAlbum.getName(), songEachAlbum.getDuration(),
                        songEachAlbum.getAlbum(), songEachAlbum.getTags(),
                        songEachAlbum.getLyrics(), songEachAlbum.getGenre(),
                        songEachAlbum.getReleaseYear(),
                        songEachAlbum.getArtist());
                newSong.setListens(songEachAlbum.getListens());
                for (int i = 0; i < songsPerArtist.size(); i++) {
                    if (songsPerArtist.get(i).getName().equals(songEachAlbum.getName())
                    && songsPerArtist.get(i).getArtist().equals(songEachAlbum.getArtist())) {
                        newSong.setListens(newSong.getListens()
                                + songsPerArtist.get(i).getListens());
                        songsPerArtist.remove(i);
                        i--;
                    }
                }
                songsPerArtist.add(newSong);
            }
        }
        // we sort the songs
        Collections.sort(songsPerArtist, Comparator
                .<Song, Integer>comparing(Song::getListens).reversed()
                .thenComparing(Song::getName));
        // we get the users that have some listens to these artists;
        ArrayList<User> usersLToCurrentArtist = new ArrayList<>();
        ArrayList<Integer> listensToCurrentArtist = new ArrayList<>();
        for (User eachUser: library.getUsers()) {
            for (int i = 0; i < eachUser.getWrapped().getArtists().size(); i++) {
                if (eachUser.getWrapped().getArtists().get(i).getUsername()
                        .equals(currentArtist.getUsername())) {
                    usersLToCurrentArtist.add(eachUser);
                    listensToCurrentArtist.add(eachUser.getWrapped().getListensArtists().get(i));
                    break;
                }
            }
        }
        // we check if we have what to show
        if (usersLToCurrentArtist.isEmpty()) {
            node.put("message", "No data to show for artist " + this.username + ".");
            return;
        }
        // sort the users
        // create a list of indices
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < listensToCurrentArtist.size(); i++) {
            indices.add(i);
        }
        // sort them based on listensToCurrentArtist
        indices.sort(Comparator.comparingInt(listensToCurrentArtist::get).reversed()
                .thenComparing(index -> usersLToCurrentArtist.get(index).getUsername()));
        // rearrange usersLToCurrentArtist based on the sorted indices
        List<User> sortedUsers = new ArrayList<>();
        for (int index : indices) {
            sortedUsers.add(usersLToCurrentArtist.get(index));
        }
        // top albums:
        ObjectNode node2 = objectMapper.createObjectNode();
        for (int i = 0; i < albumsPerArtist.size() && i < FIVE
                && albumsPerArtist.get(i).getListens() > 0; i++) {
            node2.put(albumsPerArtist.get(i).getName(),
                    albumsPerArtist.get(i).getListens());
        }
        node1.set("topAlbums", node2);
        // top songs:
        ObjectNode node3 = objectMapper.createObjectNode();
        for (int i = 0; i < songsPerArtist.size() && i < FIVE; i++) {
            node3.put(songsPerArtist.get(i).getName(),
                    songsPerArtist.get(i).getListens());
        }
        node1.set("topSongs", node3);
        // top fans:
        // we build the string array from the users
        ArrayList<String> temporary = new ArrayList<>();
        for (int i = 0; i < sortedUsers.size() && i < FIVE; i++) {
            temporary.add(sortedUsers.get(i).getUsername());
        }
        ArrayNode arrayNode;
        if (!temporary.isEmpty()) {
            arrayNode = objectMapper.valueToTree(temporary);
        } else {
            arrayNode = objectMapper.createArrayNode();
        }
        node1.set("topFans", arrayNode);
        // listeners:
        node1.put("listeners", usersLToCurrentArtist.size());
        // we add the node to the result
        node.set("result", node1);
    }

    /**
     * Method for host
     * @param objectMapper
     * @param node
     */
    public void actionForHost(final ObjectMapper objectMapper,
                              final ObjectNode node) {
        // we get the host
        Host currentHost = library.getHost(this.username);
        // we check if we have what to show
        if (currentHost.getListeners().isEmpty()) {
            node.put("message", "No data to show for host " + this.username + ".");
            return;
        }
        // new node
        ObjectNode node1 = objectMapper.createObjectNode();
        // we build the array with all episodes
        ArrayList<Episode> episodesPerHost = new ArrayList<>();
        for (Podcast podcast: library.getPodcasts()) {
            if (podcast.getOwner().equals(currentHost.getUsername())) {
                episodesPerHost.addAll(podcast.getEpisodes());
            }
        }
        // we sort the episodes
        Collections.sort(episodesPerHost, Comparator
                .<Episode, Integer>comparing(Episode::getListens).reversed()
                .thenComparing(Episode::getName));
        // top episodes:
        ObjectNode node2 = objectMapper.createObjectNode();
        for (int i = 0; i < episodesPerHost.size() && i < FIVE
                && episodesPerHost.get(i).getListens() > 0; i++) {
            node2.put(episodesPerHost.get(i).getName(),
                    episodesPerHost.get(i).getListens());
        }
        node1.set("topEpisodes", node2);
        // listeners:
        node1.put("listeners", currentHost.getListeners().size());
        // we add the node to the result
        node.set("result", node1);
    }

    /**
     * Method for normal user
     * @param objectMapper
     * @param node
     */
    public void actionForNormalUser(final ObjectMapper objectMapper,
                                    final ObjectNode node) {
        // we get the user
        User currentUser = library.getUser(this.username);
        // we check if we have what to show
        if (currentUser.getWrapped().getArtists().isEmpty()
        && currentUser.getWrapped().getEpisodes().isEmpty()
        && currentUser.getWrapped().getGenres().isEmpty()
        && currentUser.getWrapped().getSongs().isEmpty()
        && currentUser.getWrapped().getAlbums().isEmpty()) {
            node.put("message", "No data to show for user " + this.username + ".");
            return;
        }
        // new node
        ObjectNode node1 = objectMapper.createObjectNode();
        // we update users wrapped
        currentUser.getWrapped().sortAll();
        // top artists:
        ObjectNode node2 = objectMapper.createObjectNode();
        for (int i = 0; i < currentUser.getWrapped().getArtists().size()
                && i < FIVE; i++) {
            node2.put(currentUser.getWrapped().getArtists().get(i).getUsername(),
                    currentUser.getWrapped().getListensArtists().get(i));
        }
        node1.set("topArtists", node2);
        // top genres:
        ObjectNode node3 = objectMapper.createObjectNode();
        for (int i = 0; i < currentUser.getWrapped().getGenres().size()
                && i < FIVE; i++) {
            node3.put(currentUser.getWrapped().getGenres().get(i),
                    currentUser.getWrapped().getListensGenres().get(i));
        }
        node1.set("topGenres", node3);
        // top songs:
        ObjectNode node4 = objectMapper.createObjectNode();
        for (int i = 0; i < currentUser.getWrapped().getSongs().size()
                && i < FIVE; i++) {
            node4.put(currentUser.getWrapped().getSongs().get(i).getName(),
                    currentUser.getWrapped().getListensSongs().get(i));
        }
        node1.set("topSongs", node4);
        // top albums:
        ObjectNode node5 = objectMapper.createObjectNode();
        for (int i = 0; i < currentUser.getWrapped().getAlbums().size()
                && i < FIVE; i++) {
            node5.put(currentUser.getWrapped().getAlbums().get(i).getName(),
                    currentUser.getWrapped().getListensAlbums().get(i));
        }
        node1.set("topAlbums", node5);
        // top episodes:
        ObjectNode node6 = objectMapper.createObjectNode();
        for (int i = 0; i < currentUser.getWrapped().getEpisodes().size()
                && i < FIVE; i++) {
            node6.put(currentUser.getWrapped().getEpisodes().get(i).getName(),
                    currentUser.getWrapped().getListensEpisodes().get(i));
        }
        node1.set("topEpisodes", node6);
        // we add the node to the result
        node.set("result", node1);
    }

    @Override
    public void startCommand(final ObjectMapper objectMapper, final ArrayNode outputs) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("command", this.getCommand());
        node.put("user", this.getUsername());
        node.put("timestamp", this.getTimestamp());
        // update all players
        this.updateAllPlayers();
        // we get the type of user
        int typeUser = library.typeOfUser(this.username);
        if (typeUser == 1) {
            this.actionForNormalUser(objectMapper, node);
        } else if (typeUser == 2) {
            this.actionForHost(objectMapper, node);
        } else if (typeUser == THREE) {
            this.actionForArtist(objectMapper, node);
        } else {
            node.put("message", "User doesn't exists");
        }
        outputs.add(node);
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setTimestamp(final Integer timestamp) {
        this.timestamp = timestamp;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public void setPlayers(final ArrayList<Player> players) {
        this.players = players;
    }
}
