package it.units.sdm.dotsandboxes.controllers;

import it.units.sdm.dotsandboxes.core.*;

import java.util.List;

public interface IGameController {

    /**
     * Let the UI perform an initialization step. After this method has returned successfully, the UI is
     * assumed to be ready to render the game.
     *
     * @return whether the initialization process completed successfully
     */
    boolean initialize();

    /**
     * Get the number of players that will be generated and therefore will play the current game.
     *
     * @return player count [1, ]
     */
    int getPlayerCount();

    /**
     * Get the name to be assigned to the player with the passed number and color.
     *
     * @param playerNumber ordinal of the player being created
     * @return the string literal for the name to be assigned to the player being created
     */
    String getPlayerName(int playerNumber);

    /**
     * Get the number of rows and columns of the game board being created, either asking for user input or
     * returning a fixed value
     *
     * @return an integer array containing two elements: int[2] { width, height }
     */
    int[] getBoardDimensions();

    /**
     * Notify the UI to update the player visualization for the passed player instance (score counter and such).
     *
     * @param player the updated player instance
     */
    void updatePlayer(Player player);

    /**
     * Wait for the UI to receive an event of a game turn being played by the user.
     *
     * @return the Line being played in the current turn by the playing Player (determined by the game instance)
     * @see Game#makeNextMove(Line)
     */
    Line waitForLine(Player player);

    /**
     * Notify the UI to terminate the current game specifying the passed player as the winner.
     *
     * @param winner the winning player
     */
    void endGame(List<Player> winner);
}
