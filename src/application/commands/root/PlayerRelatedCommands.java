package application.commands.root;

import application.entities.player.Player;

import java.util.ArrayList;

/**
 * Abstract class for all player-related commands
 */
public abstract class PlayerRelatedCommands implements Commands {

    /**
     * Gets this users player/null if not existed
     * @param players
     * @param username
     * @return
     */
    public Player getCurrentPlayer(final ArrayList<Player> players,
                                   final String username) {
        Player currentPlayer = null;
        if (players.isEmpty()) {
            return currentPlayer;
        }
        for (Player player : players) {
            if (player.getUsername().equals(username)) {
                currentPlayer = player;
            }
        }
        return currentPlayer;
    }

    /**
     * Updates the player
     * @param currentPlayer
     * @param timestamp
     */
    public void updatePlayer(final Player currentPlayer,
                             final Integer timestamp) {
        switch (currentPlayer.getType()) {
            case "song" -> currentPlayer.checkIfEndedSong(timestamp);
            case "podcast" -> currentPlayer.checkIfEndedPodcast(timestamp);
            case "album" -> currentPlayer.checkIfEndedAlbum(timestamp);
            case "playlist" -> currentPlayer.checkIfEndedPlaylist(timestamp);
            default -> { }
        }
    }
}
