package application.entities.player;

import lombok.Getter;

/**
 * Class that holds the current situation for a podcast in a player
 */
@Getter
public final class PlayerPodcast {
    private String nameEpisode;
    private Integer minute;

    /**
     * Constructor
     * @param nameEpisode - episode name
     * @param minute - the minute it is currently
     */
    public PlayerPodcast(final String nameEpisode, final Integer minute) {
        this.nameEpisode = nameEpisode;
        this.minute = minute;
    }

    public void setNameEpisode(final String nameEpisode) {
        this.nameEpisode = nameEpisode;
    }

    public void setMinute(final Integer minute) {
        this.minute = minute;
    }

}
