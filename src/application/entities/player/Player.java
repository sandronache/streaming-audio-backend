package application.entities.player;

import application.entities.library.Episode;
import application.entities.library.Library;
import application.entities.library.Playlist;
import application.entities.library.Podcast;
import application.entities.library.Song;
import application.entities.library.users.artist.Album;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Class that represents the player for a user
 */
@Getter
public final class Player {
    private String username;
    private String type;
    // current situation on each podcast
    private ArrayList<PlayerPodcast> situationPodcasts;
    private Library library;
    private Playlist playlist;
    private Album album;
    private Song song;
    // name song/ podcast/ playlist/ album
    private String name;
    // artist/ owner
    private String artist;
    // just for songs and current song in playlist
    private Integer remainedTime;
    private Integer timestamp;
    private Integer repeat;
    private boolean shuffle;
    private boolean paused;
    // seed and new shuffled indices
    private Integer seed;
    private ArrayList<Integer> indicesShuffle;

    /**
     * Constructor for new player
     * @param username
     * @param library
     */
    public Player(final String username, final Library library) {
        this.username = username;
        this.library = library;
        this.situationPodcasts = new ArrayList<>();
        for (Podcast podcast : library.getPodcasts()) {
            PlayerPodcast currentPodcast = new PlayerPodcast(
                    podcast.getEpisodes().get(0).getName(), 0);
            situationPodcasts.add(currentPodcast);
        }
        this.indicesShuffle = new ArrayList<>();
    }

    /**
     * Calculates indices for shuffle
     * @param size
     */
    public void calculateIndicesShuffle(final Integer size) {
        for (int i = 0; i < size; i++) {
            indicesShuffle.add(i);
        }
        Collections.shuffle(indicesShuffle, new Random(seed));
    }

    /**
     * New status playlist
     * @param newSong
     * @param newName
     * @param newType
     * @param newPlaylist
     * @param newRemainedTime
     * @param newTimestamp
     * @param newRepeat
     * @param newShuffle
     * @param newPaused
     */
    public void setNewStatusPlaylist(final Song newSong, final String newName,
                                     final String newType, final Playlist newPlaylist,
                                     final Integer newRemainedTime, final Integer newTimestamp,
                                     final Integer newRepeat, final boolean newShuffle,
                                     final boolean newPaused) {
        this.song = newSong;
        this.name = newName;
        this.type = newType;
        this.playlist = newPlaylist;
        this.remainedTime = newRemainedTime;
        this.timestamp = newTimestamp;
        this.repeat = newRepeat;
        this.shuffle = newShuffle;
        this.paused = newPaused;
        this.indicesShuffle = new ArrayList<>();
    }

    /**
     * New status album
     * @param newSong
     * @param newName
     * @param newType
     * @param newAlbum
     * @param newRemainedTime
     * @param newTimestamp
     * @param newRepeat
     * @param newShuffle
     * @param newPaused
     */
    public void setNewStatusAlbum(final Song newSong, final String newName,
                                     final String newType, final Album newAlbum,
                                     final Integer newRemainedTime, final Integer newTimestamp,
                                     final Integer newRepeat, final boolean newShuffle,
                                     final boolean newPaused) {
        this.song = newSong;
        this.name = newName;
        this.type = newType;
        this.album = newAlbum;
        this.remainedTime = newRemainedTime;
        this.timestamp = newTimestamp;
        this.repeat = newRepeat;
        this.shuffle = newShuffle;
        this.paused = newPaused;
        this.indicesShuffle = new ArrayList<>();
    }

    /**
     * New status podcast
     * @param newName
     * @param newArtist
     * @param newType
     * @param newTimestamp
     * @param newRepeat
     * @param newShuffle
     * @param newPaused
     */
    public void setNewStatusPodcast(final String newName, final String newArtist,
                                    final String newType, final Integer newTimestamp,
                                    final Integer newRepeat, final boolean newShuffle,
                                    final boolean newPaused) {
        this.name = newName;
        this.artist = newArtist;
        this.type = newType;
        this.timestamp = newTimestamp;
        this.repeat = newRepeat;
        this.shuffle = newShuffle;
        this.paused = newPaused;
        this.indicesShuffle = new ArrayList<>();
    }

    /**
     * New status song
     * @param newSong
     * @param newName
     * @param newType
     * @param newRemainedTime
     * @param newTimestamp
     * @param newRepeat
     * @param newShuffle
     * @param newPaused
     */
    public void setNewStatusSong(final Song newSong, final String newName,
                                 final String newType, final Integer newRemainedTime,
                                 final Integer newTimestamp, final Integer newRepeat,
                                 final boolean newShuffle, final boolean newPaused) {
        this.song = newSong;
        this.name = newName;
        this.type = newType;
        this.remainedTime = newRemainedTime;
        this.timestamp = newTimestamp;
        this.repeat = newRepeat;
        this.shuffle = newShuffle;
        this.paused = newPaused;
        this.indicesShuffle = new ArrayList<>();
    }

    /**
     * Calculates the remaining time from the current episode
     * @return
     */
    public int remainedTimePodcast() {
        for (int i = 0; i < library.getPodcasts().size(); i++) {
            Podcast podcast = library.getPodcasts().get(i);
            if (name.equals(podcast.getName())) {
                for (int j = 0; j < podcast.getEpisodes().size(); j++) {
                    Episode episodeInput = podcast.getEpisodes().get(j);
                    if (situationPodcasts.get(i).getNameEpisode()
                            .equals(episodeInput.getName())) {
                        return episodeInput.getDuration()
                                - situationPodcasts.get(i).getMinute();
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Gets the index in the playlist for the current song in player
     * @return
     */
    public int playlistIndex() {
        for (int i = 0; i < playlist.getSongs().size(); i++) {
            if (playlist.getSongs().get(i).equals(this.song)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the index on the album for the current song in player
     * @return
     */
    public int albumIndex() {
        for (int i = 0; i < album.getSongs().size(); i++) {
            if (album.getSongs().get(i).equals(this.song)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the index in the playlist/album for the current song in player
     * from the shuffled indices
     * @param songIndex
     * @return
     */
    public int getIndexFromShuffle(final int songIndex) {
        for (int i = 0; i < indicesShuffle.size(); i++) {
            if (indicesShuffle.get(i) == songIndex) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Updates player when loaded an album not on shuffle
     * @param temp
     * @param songIndex
     */
    public void albumRepeat(final Integer temp, final int songIndex) {
        int temporary = temp;
        int index = songIndex;
        if (repeat == 0 && index == (album.getSongs().size() - 1)) {
            this.setNewStatusAlbum(this.song, "", "album",
                    album, 0, timestamp,
                    0, false, true);
        } else {
            while (temporary >= remainedTime) {
                temporary -= remainedTime;
                if (index == (album.getSongs().size() - 1)) {
                    if (repeat == 0) {
                        this.setNewStatusAlbum(this.song, "", "album",
                                album, 0, timestamp,
                                0, false, true);
                        return;
                    } else {
                        index = 0;
                        this.song = album.getSongs().get(index);
                        this.remainedTime = song.getDuration();
                        // add to the wrapped
                        library.addSongForUser(this.username, this.song);
                        continue;
                    }
                }
                index = index + 1;
                this.song = album.getSongs().get(index);
                this.remainedTime = song.getDuration();
                // add to the wrapped
                library.addSongForUser(this.username, this.song);
            }
            remainedTime -= temporary;
        }
    }

    /**
     * Updates player when loaded an album on shuffle
     * @param temp
     * @param songIndex
     */
    public void albumRepeatShuffle(final Integer temp, final int songIndex) {
        int temporary = temp;
        int index = songIndex;
        int songIndexShuffle = this.getIndexFromShuffle(index);
        if (repeat == 0 && songIndexShuffle == (indicesShuffle.size() - 1)) {
            this.setNewStatusAlbum(this.song, "", "album",
                    album, 0, timestamp,
                    0, false, true);
        } else {
            while (temporary >= remainedTime) {
                temporary -= remainedTime;
                if (songIndexShuffle == (indicesShuffle.size() - 1)) {
                    if (repeat == 0) {
                        this.setNewStatusAlbum(this.song, "", "album",
                                album, 0, timestamp,
                                0, false, true);
                        return;
                    } else {
                        songIndexShuffle = 0;
                        index = indicesShuffle.get(songIndexShuffle);
                        this.song = album.getSongs().get(index);
                        this.remainedTime = song.getDuration();
                        // add to the wrapped
                        library.addSongForUser(this.username, this.song);
                        continue;
                    }
                }
                songIndexShuffle = songIndexShuffle + 1;
                index = indicesShuffle.get(songIndexShuffle);
                this.song = album.getSongs().get(index);
                this.remainedTime = song.getDuration();
                // add to the wrapped
                library.addSongForUser(this.username, this.song);
            }
            remainedTime -= temporary;
        }
    }

    /**
     * Updates player when loaded an album (start point)
     * @param newTimestamp
     */
    public void checkIfEndedAlbum(final Integer newTimestamp) {
        if (this.getAlbum().getSongs().isEmpty()) {
            this.setNewStatusAlbum(this.song, "", "album",
                    album, 0, timestamp,
                    0, false, true);
            return;
        }
        int temp = newTimestamp - this.timestamp;
        if (temp == 0) {
            return;
        }
        if (this.remainedTime > temp) {
            this.remainedTime -= temp;
        } else {
            if (repeat == 2) {
                temp -= remainedTime;
                // add to the wrapped
                for (int i = 0; i < ((temp / this.song.getDuration()) + 1); i++) {
                    library.addSongForUser(this.username, this.song);
                }
                this.remainedTime = this.song.getDuration()
                        - (temp % this.song.getDuration());
            } else {
                int songIndex = this.albumIndex();
                if (shuffle) {
                    this.albumRepeatShuffle(temp, songIndex);
                } else {
                    this.albumRepeat(temp, songIndex);
                }
            }
        }
        this.setTimestamp(newTimestamp);
    }

    /**
     * Updates player when loaded a playlist not on shuffle
     * @param temp
     * @param songIndex
     */
    public void playlistRepeat(final Integer temp, final int songIndex) {
        int temporary = temp;
        int index = songIndex;
        if (repeat == 0 && index == (playlist.getSongs().size() - 1)) {
            this.setNewStatusPlaylist(this.song, "", "playlist",
                    playlist, 0, timestamp,
                    0, false, true);
        } else {
            while (temporary >= remainedTime) {
                temporary -= remainedTime;
                if (index == (playlist.getSongs().size() - 1)) {
                    if (repeat == 0) {
                        this.setNewStatusPlaylist(this.song, "",
                                "playlist", playlist, 0, timestamp,
                                0, false, true);
                        return;
                    } else {
                        index = 0;
                        this.song = playlist.getSongs().get(index);
                        this.remainedTime = song.getDuration();
                        // add to the wrapped
                        library.addSongForUser(this.username, this.song);
                        continue;
                    }
                }
                index = index + 1;
                this.song = playlist.getSongs().get(index);
                this.remainedTime = song.getDuration();
                // add to the wrapped
                library.addSongForUser(this.username, this.song);
            }
            remainedTime -= temporary;
        }
    }

    /**
     * Updates player when loaded a playlist on shuffle
     * @param temp
     * @param songIndex
     */
    public void playlistRepeatShuffle(final Integer temp, final int songIndex) {
        int temporary = temp;
        int index = songIndex;
        int songIndexShuffle = this.getIndexFromShuffle(index);
        if (repeat == 0 && songIndexShuffle == (indicesShuffle.size() - 1)) {
            this.setNewStatusPlaylist(this.song, "", "playlist",
                    playlist, 0, timestamp,
                    0, false, true);
        } else {
            while (temporary >= remainedTime) {
                temporary -= remainedTime;
                if (songIndexShuffle == (indicesShuffle.size() - 1)) {
                    if (repeat == 0) {
                        this.setNewStatusPlaylist(this.song, "",
                                "playlist", playlist, 0,
                                timestamp, 0, false, true);
                        return;
                    } else {
                        songIndexShuffle = 0;
                        index = indicesShuffle.get(songIndexShuffle);
                        this.song = playlist.getSongs().get(index);
                        this.remainedTime = song.getDuration();
                        // add to the wrapped
                        library.addSongForUser(this.username, this.song);
                        continue;
                    }
                }
                songIndexShuffle = songIndexShuffle + 1;
                index = indicesShuffle.get(songIndexShuffle);
                this.song = playlist.getSongs().get(index);
                this.remainedTime = song.getDuration();
                // add to the wrapped
                library.addSongForUser(this.username, this.song);
            }
            remainedTime -= temporary;
        }
    }

    /**
     * Updates player when loaded a playlist (start point)
     * @param newTimestamp
     */
    public void checkIfEndedPlaylist(final Integer newTimestamp) {
        if (this.playlist.getSongs().isEmpty()) {
            this.setNewStatusPlaylist(this.song, "",
                    "playlist", playlist, 0,
                    timestamp, 0, false, true);
            return;
        }
        int temp = newTimestamp - this.timestamp;
        if (temp == 0) {
            return;
        }
        if (this.remainedTime > temp) {
            this.remainedTime -= temp;
        } else {
             if (repeat == 2) {
                temp -= remainedTime;
                 // add to the wrapped
                 for (int i = 0; i < ((temp / this.song.getDuration()) + 1); i++) {
                     library.addSongForUser(this.username, this.song);
                 }
                this.remainedTime = this.song.getDuration()
                        - (temp % this.song.getDuration());
            } else {
                int songIndex = this.playlistIndex();
                if (shuffle) {
                    this.playlistRepeatShuffle(temp, songIndex);
                } else {
                    this.playlistRepeat(temp, songIndex);
                }
            }
        }
        this.setTimestamp(newTimestamp);
    }

    /**
     * Gets episode name from the current situation of the podcast
     * @return
     */
    public String episodeName() {
        for (int i = 0; i < library.getPodcasts().size(); i++) {
            Podcast podcast = library.getPodcasts().get(i);
            if (name.equals(podcast.getName())) {
                    return situationPodcasts.get(i).getNameEpisode();
            }
        }
        return null;
    }

    /**
     * Finds the index of the current podcast in the library if bool true
     * else finds the index of the episode
     * @param bool
     * @return
     */
    public int podcastIndex(final boolean bool) {
        for (int i = 0; i < library.getPodcasts().size(); i++) {
            Podcast podcast = library.getPodcasts().get(i);
            if (name.equals(podcast.getName())
                    && artist.equals(podcast.getOwner())) {
                for (int j = 0; j < podcast.getEpisodes().size(); j++) {
                    Episode episodeInput = podcast.getEpisodes().get(j);
                    if (situationPodcasts.get(i)
                            .getNameEpisode().equals(episodeInput.getName())) {
                        if (bool) {
                            return i;
                        } else {
                            return j;
                        }
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Updates player when loaded a podcast with repeat 0
     * @param temp
     */
    public void podcastNoRepeat(final Integer temp) {
        int temporary = temp;
        int i = this.podcastIndex(true);
        int j = this.podcastIndex(false);
        Podcast podcast = library.getPodcasts().get(i);
        Episode episodeInput = podcast.getEpisodes().get(j);
        if (j == (podcast.getEpisodes().size() - 1)) {
            setNewStatusPodcast("", "",
                    "podcast", timestamp, 0, false,
                    true);
            situationPodcasts.get(i).setMinute(0);
            situationPodcasts.get(i).setNameEpisode(
                    podcast.getEpisodes().get(0).getName());
        } else {
            while (situationPodcasts.get(i).getMinute() + temporary
                    >= episodeInput.getDuration()) {
                if (j == (podcast.getEpisodes().size() - 1)) {
                    setNewStatusPodcast("", "",
                            "podcast", timestamp, 0,
                            false, true);
                    situationPodcasts.get(i).setMinute(0);
                    situationPodcasts.get(i).setNameEpisode(
                            podcast.getEpisodes().get(0).getName());
                    return;
                }
                temporary = temporary + situationPodcasts.get(i).getMinute()
                        - episodeInput.getDuration();
                j = j + 1;
                episodeInput = podcast.getEpisodes().get(j);
                situationPodcasts.get(i).setMinute(0);
                situationPodcasts.get(i).setNameEpisode(episodeInput.getName());
                // add to the wrapped
                library.addEpisodeForUserAndHost(this.username,
                        episodeInput, podcast.getOwner());
            }
            situationPodcasts.get(i).setMinute(temporary);
        }
    }

    /**
     * Updates player when loaded a podcast with repeat 1
     * @param temp
     */
    public void podcastRepeatOnce(final Integer temp) {
        int temporary = temp;
        int i = this.podcastIndex(true);
        int j = this.podcastIndex(false);
        Podcast podcast = library.getPodcasts().get(i);
        Episode episodeInput = podcast.getEpisodes().get(j);
        int newTemp = situationPodcasts.get(i).getMinute()
                + temporary - episodeInput.getDuration();
        if (newTemp >= episodeInput.getDuration()) {
            newTemp -= episodeInput.getDuration();
            if (j == (podcast.getEpisodes().size() - 1)) {
                setNewStatusPodcast("", "", "podcast",
                        timestamp, 0, false, true);
                situationPodcasts.get(i).setMinute(0);
                situationPodcasts.get(i).setNameEpisode(
                        podcast.getEpisodes().get(0).getName());
            } else {
                situationPodcasts.get(i).setMinute(0);
                situationPodcasts.get(i).setNameEpisode(
                        podcast.getEpisodes().get(j + 1).getName());
                // add to the wrapped
                library.addEpisodeForUserAndHost(this.username,
                        podcast.getEpisodes().get(j + 1), podcast.getOwner());
                this.repeat = 0;
                this.podcastNoRepeat(newTemp);
            }
        } else {
            situationPodcasts.get(i).setMinute(newTemp);
            this.repeat = 0;
        }
    }

    /**
     * Updates player when loaded a podcast (start point)
     * @param newTimestamp
     */
    public void checkIfEndedPodcast(final Integer newTimestamp) {
        int temporary = newTimestamp;
        int temp = temporary - this.timestamp;
        if (temp == 0) {
            return;
        }
        int i = this.podcastIndex(true);
        int j = this.podcastIndex(false);
        Podcast podcast = library.getPodcasts().get(i);
        Episode episodeInput = podcast.getEpisodes().get(j);
        if (situationPodcasts.get(i).getMinute() + temp
                <= episodeInput.getDuration()) {
            situationPodcasts.get(i).setMinute(situationPodcasts.get(i).getMinute() + temp);
        } else {
            if (repeat == 0) {
                this.podcastNoRepeat(temp);
            } else if (repeat == 1) {
                this.podcastRepeatOnce(temp);
            } else {
                int newTemp = situationPodcasts.get(i).getMinute()
                        + temp - episodeInput.getDuration();
                // add to the wrapped
                for (int k = 0; k < ((newTemp / episodeInput.getDuration()) + 1); k++) {
                    library.addEpisodeForUserAndHost(this.username,
                            episodeInput, podcast.getOwner());
                }
                situationPodcasts.get(i).setMinute(
                        newTemp % episodeInput.getDuration());
            }
        }
        this.setTimestamp(temporary);
    }

    /**
     * Updates player when loaded a song (start point)
     * @param newTimestamp
     */
    public void checkIfEndedSong(final Integer newTimestamp) {
        int temporary = newTimestamp;
        if ((temporary - this.getTimestamp()) == 0) {
            return;
        }
        int temp = this.getRemainedTime() - (temporary
                - this.getTimestamp());
        if (temp < 0) {
            if (repeat == 0 || (repeat == 1 && ((-temp)
                    >= this.song.getDuration()))) {
                this.setNewStatusSong(this.song, "",
                        "song", 0, temporary, 0,
                        false, true);
            } else if (repeat == 1) {
                this.remainedTime = this.song.getDuration()
                        + temp;
                this.repeat = 0;
                // add to the wrapped
                library.addSongForUser(this.username, this.song);
            } else {
                // add to the wrapped
                for (int i = 0; i < (((-temp) / this.song.getDuration()) + 1); i++) {
                    library.addSongForUser(this.username, this.song);
                }
                this.remainedTime = this.song.getDuration()
                        - ((-temp) % this.song.getDuration());
            }
        } else {
            this.setRemainedTime(temp);
        }
        this.setTimestamp(temporary);
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setRemainedTime(final Integer remainedTime) {
        this.remainedTime = remainedTime;
    }

    public void setTimestamp(final Integer timestamp) {
        this.timestamp = timestamp;
    }

    public void setRepeat(final Integer repeat) {
        this.repeat = repeat;
    }

    public void setShuffle(final boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setPaused(final boolean paused) {
        this.paused = paused;
    }

    public void setSituationPodcasts(final ArrayList<PlayerPodcast> situationPodcasts) {
        this.situationPodcasts = situationPodcasts;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public void setPlaylist(final Playlist playlist) {
        this.playlist = playlist;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public void setSeed(final Integer seed) {
        this.seed = seed;
    }

    public void setIndicesShuffle(final ArrayList<Integer> indicesShuffle) {
        this.indicesShuffle = indicesShuffle;
    }

    public void setSong(final Song song) {
        this.song = song;
    }

    public void setAlbum(final Album album) {
        this.album = album;
    }
}
