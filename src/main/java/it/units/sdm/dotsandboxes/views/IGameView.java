package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.core.Game;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Semaphore;

/**
 * Abstract class containing the basic features each view must have to properly work with the controller.
 */
public abstract class IGameView implements Runnable {

    protected IGameController controllerReference;
    protected Game gameStateReference;
    private boolean isInitialized;
    private boolean isConfigured;
    private Semaphore isRefreshingUISem;

    /**
     * Initializes the view.
     *
     * @return true if initialization was successful, false otherwise.
     */
    public final boolean init(final IGameController controllerReference) {
        isInitialized = false;
        isConfigured = false;
        isRefreshingUISem = new Semaphore(0);
        isInitialized = assignControllerReference(controllerReference) && finishInit();
        return isInitialized;
    }

    /** The finishing steps needed in each implementation to complete the initialization.
     * @return if the finishing initialization steps have been performed correctly.
     */
    protected abstract boolean finishInit();

    private boolean assignControllerReference(final IGameController controllerReference) {
        try {
            Objects.requireNonNull(controllerReference);
            this.controllerReference = controllerReference;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Configures the state of the game into the view. View must have been already initialized.
     *
     * @return true if the configuration was successful, false otherwise.
     */
    public final boolean configure(final Game gameStateReference) {
        isConfigured = isInitialized && assignGameStateReference(gameStateReference) && finishConfigure();
        return isConfigured;
    }

    private boolean assignGameStateReference(final Game gameStateReference) {
        try {
            Objects.requireNonNull(gameStateReference);
            this.gameStateReference = gameStateReference;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /** The finishing steps needed in each implementation to complete the configuration.
     * @return if the finishing configuration steps have been performed correctly.
     */
    protected abstract boolean finishConfigure();

    /**
     * Starts the display loop of the game. View must be initialized and configured.
     */
    public void startGameUI() throws InvalidInputException {
        if (!isInitializedAndConfigured()) {
            throw new InvalidInputException("View was not initialized and configured properly");
        }
        displayGameUI();
    }

    protected boolean isInitializedAndConfigured() {
        return isInitialized && isConfigured;
    }

    protected abstract void displayGameUI();

    /**
     * Prompts the user to input the gamemode.
     *
     * @return "PVP" : Against players
     * "PVE" : Against the computer
     */
    public abstract String promptForGamemode();

    /** Prompts the user for the desired number of players.
     * @return the desired number of players.
     */
    public abstract String promptForNumberOfPlayers();

    /** Prompts the user for the desired dimensions of the board.
     * @return the dimensions of the board in the form: int[2] { height, width }.
     */
    public String[] promptForBoardDimensions() {
        return new String[]{promptForBoardHeight(), promptForBoardWidth()};
    }

    /** Prompts the user for the desired height of the board.
     * @return the desired height of the board.
     */
    public abstract String promptForBoardHeight();

    /** Prompts the user for the desired width of the board.
     * @return the desired width of the board.
     */
    public abstract String promptForBoardWidth();

    /** Prompts the player for its name.
     * @param playerNumber - the ordinal of the player.
     * @return the name chosen by the player.
     */
    public abstract String promptForPlayerName(int playerNumber);

    /**
     * Prompts the user regarding what to do at the end of the game.
     *
     * @return "NEW" : New game
     * "END" : End game
     */
    public abstract String promptForPostGameIntent();

    /**
     * Displays a message to the user.
     *
     * @param message Text to display.
     */
    public abstract void displayMessage(String message);

    /**
     * Displays a blocking warning to the user.
     *
     * @param message Text to display.
     */
    public abstract void displayWarning(String message);

    /**
     * Displays the winner(s) of the game.
     */
    public abstract void displayResults();

    public void signalWhenUIRefreshed(){
        try {
            isRefreshingUISem.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void signalUIHasRefreshed(){
        isRefreshingUISem.release();
    }

}
