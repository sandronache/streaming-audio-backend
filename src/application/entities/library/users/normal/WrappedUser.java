package application.entities.library.users.normal;

import application.entities.library.Episode;
import application.entities.library.Song;
import application.entities.library.users.artist.Album;
import application.entities.library.users.artist.Artist;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class for holding the wrapped of a normal user
 */
@Getter
public final class WrappedUser {
    private ArrayList<Artist> artists;
    private ArrayList<Integer> listensArtists;
    private ArrayList<String> genres;
    private ArrayList<Integer> listensGenres;
    private ArrayList<Song> songs;
    private ArrayList<Integer> listensSongs;
    private ArrayList<Album> albums;
    private ArrayList<Integer> listensAlbums;
    private ArrayList<Episode> episodes;
    private ArrayList<Integer> listensEpisodes;

    /**
     * Default constructor modified
     */
    public WrappedUser() {
        artists = new ArrayList<>();
        listensArtists = new ArrayList<>();
        genres = new ArrayList<>();
        listensGenres = new ArrayList<>();
        songs = new ArrayList<>();
        listensSongs = new ArrayList<>();
        albums = new ArrayList<>();
        listensAlbums = new ArrayList<>();
        episodes = new ArrayList<>();
        listensEpisodes = new ArrayList<>();
    }

    /**
     * Adds a new song to the list or just one more listening
     * @param songParam
     */
    public void addSong(final Song songParam) {
        for (int i = 0; i < songs.size(); i++) {
            if (songParam.getName().equals(songs.get(i).getName())
            && songParam.getArtist().equals(songs.get(i).getArtist())) {
                Integer value = listensSongs.get(i);
                listensSongs.set(i, value + 1);
                return;
            }
        }
        songs.add(songParam);
        listensSongs.add(1);
    }

    /**
     * Adds a new genre to the list or just one more listening
     * @param genreParam
     */
    public void addGenre(final String genreParam) {
        for (int i = 0; i < genres.size(); i++) {
            if (genreParam.equals(genres.get(i))) {
                Integer value = listensGenres.get(i);
                listensGenres.set(i, value + 1);
                return;
            }
        }
        genres.add(genreParam);
        listensGenres.add(1);
    }

    /**
     * Adds a new artist to the list or just one more listening
     * @param artistParam
     */
    public void addArtist(final Artist artistParam) {
        for (int i = 0; i < artists.size(); i++) {
            if (artistParam.getUsername()
                    .equals(artists.get(i).getUsername())) {
                Integer value = listensArtists.get(i);
                listensArtists.set(i, value + 1);
                return;
            }
        }
        artists.add(artistParam);
        listensArtists.add(1);
    }

    /**
     * Adds a new album to the list or just one more listening
     * @param albumParam
     */
    public void addAlbum(final Album albumParam) {
        for (int i = 0; i < albums.size(); i++) {
            if (albumParam.getName().equals(albums.get(i).getName())) {
                Integer value = listensAlbums.get(i);
                listensAlbums.set(i, value + 1);
                return;
            }
        }
        albums.add(albumParam);
        listensAlbums.add(1);
    }

    /**
     * Adds a new episode to the list or just one more listening
     * @param episodeParam
     */
    public void addEpisode(final Episode episodeParam) {
        for (int i = 0; i < episodes.size(); i++) {
            if (episodeParam.equals(episodes.get(i))) {
                Integer value = listensEpisodes.get(i);
                listensEpisodes.set(i, value + 1);
                return;
            }
        }
        episodes.add(episodeParam);
        listensEpisodes.add(1);
    }

    /**
     * Method that sorts all descending
     */
    public void sortAll() {
        sortEach(artists, listensArtists, Comparator.comparing(Artist::getUsername));
        sortEach(genres, listensGenres, Comparator.naturalOrder());
        sortEach(songs, listensSongs, Comparator.comparing(Song::getName));
        sortEach(albums, listensAlbums, Comparator.comparing(Album::getName));
        sortEach(episodes, listensEpisodes, Comparator.comparing(Episode::getName));
    }

    /**
     * Generic method that sorts for each one
     * @param entities
     * @param listens
     * @param <T>
     */
    private static <T> void sortEach(final List<T> entities,
                                     final List<Integer> listens,
                                     final Comparator<T> customComp) {
        // we create a pairs list
        List<ELPair<T>> elPairs = new ArrayList<>();
        // the pairs
        for (int i = 0; i < entities.size(); i++) {
            elPairs.add(new ELPair<>(entities.get(i), listens.get(i)));
        }
        // sort descending based on listeners
        Collections.sort(elPairs,
                Comparator.<ELPair<T>, Integer>comparing(ELPair::getListeners)
                        .reversed()
                        .thenComparing(ELPair::getEntity, customComp));
        // update originals
        for (int i = 0; i < entities.size(); i++) {
            ELPair<T> pair = elPairs.get(i);
            entities.set(i, pair.getEntity());
            listens.set(i, pair.getListeners());
        }
    }

    /**
     * Inner class representing entity-listeners pair
     * @param <T>
     */
    @Getter
    private static class ELPair<T> {
        private final T entity;
        private final int listeners;

         ELPair(final T entity, final int listeners) {
            this.entity = entity;
            this.listeners = listeners;
        }

    }

    public void setArtists(final ArrayList<Artist> artists) {
        this.artists = artists;
    }

    public void setListensArtists(final ArrayList<Integer> listensArtists) {
        this.listensArtists = listensArtists;
    }

    public void setGenres(final ArrayList<String> genres) {
        this.genres = genres;
    }

    public void setListensGenres(final ArrayList<Integer> listensGenres) {
        this.listensGenres = listensGenres;
    }

    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setListensSongs(final ArrayList<Integer> listensSongs) {
        this.listensSongs = listensSongs;
    }

    public void setAlbums(final ArrayList<Album> albums) {
        this.albums = albums;
    }

    public void setListensAlbums(final ArrayList<Integer> listensAlbums) {
        this.listensAlbums = listensAlbums;
    }

    public void setEpisodes(final ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }

    public void setListensEpisodes(final ArrayList<Integer> listensEpisodes) {
        this.listensEpisodes = listensEpisodes;
    }

}
