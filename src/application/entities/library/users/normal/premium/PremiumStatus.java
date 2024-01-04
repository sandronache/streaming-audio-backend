package application.entities.library.users.normal.premium;

import application.entities.library.users.artist.Artist;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Class for premium situation for each user
 */
@Getter
public final class PremiumStatus {
    private ArrayList<Artist> artists;
    private ArrayList<ArtistPremiumStatus> states;

    /**
     * Constructor
     */
    public PremiumStatus() {
        artists = new ArrayList<>();
        states = new ArrayList<>();
    }

    /**
     * Method that returns the total number of songs played
     * @return
     */
    public int totalNrOfSongs() {
        int total = 0;
        for (int i = 0; i < states.size(); i++) {
            for (int j = 0; j < states.get(i).getTimes().size(); j++) {
                total += states.get(i).getTimes().get(j);
            }
        }
        return total;
    }

    public void setArtists(final ArrayList<Artist> artists) {
        this.artists = artists;
    }

    public void setStates(final ArrayList<ArtistPremiumStatus> states) {
        this.states = states;
    }

}
