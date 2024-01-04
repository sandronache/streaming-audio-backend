package application.entities.library.users.normal.premium;

import application.entities.library.Song;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for the situation for each artist for each normal user
 * premium status
 */
@Getter
public final class ArtistPremiumStatus {
    private ArrayList<Song> songs;
    private ArrayList<Integer> times;

    /**
     * Constructor
     */
    public ArtistPremiumStatus(final Song songParam) {
        songs = new ArrayList<>();
        songs.add(songParam);
        times = new ArrayList<>();
        times.add(1);
    }

    /**
     * This method adds a new song
     * @param songParam
     */
    public void addNewPair(final Song songParam) {
        songs.add(songParam);
        times.add(1);
    }

    /**
     * This method calculates the total of times that all songs
     * were played
     * @return
     */
    public Integer totalNrOfSongsPerArtist() {
        Integer total = 0;
         for (Integer i: times) {
             total += i;
         }
         return total;
    }

    /**
     * This adds the correspondent part of the revenue for each song
     * @param revenue
     */
    public void addRevenueToEachSong(final Double revenue) {
        // we calculate the total number of plays
        int totalPlays = this.totalNrOfSongsPerArtist();
        // for each song we add the part of the revenue
        for (int i = 0; i < songs.size(); i++) {
            songs.get(i).addRevenue((revenue / totalPlays)
                    * times.get(i));
        }
    }


    public void setSongs(final ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void setTimes(final ArrayList<Integer> times) {
        this.times = times;
    }

}
