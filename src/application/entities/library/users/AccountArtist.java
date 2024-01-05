package application.entities.library.users;

import lombok.Getter;

/**
 * Class for the account of all artists
 */
@Getter
public final class AccountArtist {
    private String username;
    private Double songRevenue;
    private Double merchRevenue;
    private Double totalRevenue;
    private boolean playedOrNot;

    /**
     * Constructor
     * @param usernameParam
     */
    public AccountArtist(final String usernameParam) {
        this.username = usernameParam;
        this.songRevenue = (double) 0;
        this.merchRevenue = (double) 0;
        this.totalRevenue = (double) 0;
        playedOrNot = false;
    }

    /**
     * This method rounds the doubles to 2 decimals
     */
    public void roundDoubles() {
        songRevenue = Math.round(songRevenue * 100.0) / 100.0;
        merchRevenue = Math.round(merchRevenue * 100.0) / 100.0;
    }

    /**
     * Changes the "played status
     */
    public void gotPlayed() {
        playedOrNot = true;
    }

    /**
     * Method that adds revenue for a song
     * @param revenueParam
     */
    public void addRevenueSong(final Double revenueParam) {
        totalRevenue += revenueParam;
        songRevenue += revenueParam;
    }

    /**
     * Method that adds revenue for a merch
     * @param revenueParam
     */
    public void addRevenueMerchandise(final Double revenueParam) {
        totalRevenue += revenueParam;
        merchRevenue += revenueParam;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setSongRevenue(final Double songRevenue) {
        this.songRevenue = songRevenue;
    }

    public void setMerchRevenue(final Double merchRevenue) {
        this.merchRevenue = merchRevenue;
    }

    public void setPlayedOrNot(final boolean playedOrNot) {
        this.playedOrNot = playedOrNot;
    }

    public void setTotalRevenue(final Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
