package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.core.Game;

public abstract class IGameView implements Runnable {

    protected IGameController controllerReference;
    protected Game gameStateReference;
    protected boolean isInitialized = false;
    protected boolean isConfigured = false;

    /** Initializes the view.
     * @return true if initialization was successful, false otherwise.
     */
    public abstract boolean init(final IGameController controllerReference);

    /** Configures the state of the game into the view. View must have been already initialized.
     * @return true if the configuration was successful, false otherwise.
     */
    public abstract boolean configure(final Game gameStateReference);

    /**
     * Starts the display loop of the game. View must be initialized and configured.
     */
    public abstract void startGameUI();

    /** Prompts the user to input the gamemode.
     * @return "PVP" : Against players
     *          "PVE" : Against the computer
     */
    public abstract String promptForGamemode();

    public abstract String promptForNumberOfPlayers();

    public String[] promptForBoardDimensions(){
        return new String[]{promptForBoardHeight(), promptForBoardWidth()};
    }

    public abstract String promptForBoardHeight();

    public abstract String promptForBoardWidth();

    public String[] promptForPlayersNames(int numOfPlayers){
        String[] names = new String[numOfPlayers];
        for (int i = 0; i < numOfPlayers; i++) {
            names[i] = promptForPlayerName(i);
        }
        return names;
    }

    public abstract String promptForPlayerName(int playerNumber);

    /** Prompts the user regarding what to do at the end of the game.
     * @return "NEW" : New game
     *          "END" : End game
     */
    public abstract String promptForPostGameIntent();

    /** Displays a message to the user.
     * @param message Text to display.
     */
    public abstract void displayMessage(String message);

    /** Displays a blocking warning to the user.
     * @param message Text to display.
     */
    public abstract void displayWarning(String message);

    /** Displays the winner(s) of the game.
     */
    public abstract void displayResults();

}
