package it.units.sdm.dotsandboxes.controllers;

import it.units.sdm.dotsandboxes.core.*;
import it.units.sdm.dotsandboxes.views.IGameView;

import java.util.ArrayList;
import java.util.List;

public abstract class IGameController {

    private final IGameView view;
    private Game game;

    public IGameController(final IGameView view) {
        this.view = view;
    }


    /**
     * Let the controller and the UI perform an initialization step. After this method has returned successfully, the UI is
     * assumed to be ready to render the game and the game is ready to be started.
     *
     * @return whether the initialization process completed successfully
     */
    public abstract boolean initialize();

    /**
     * Acquires from the user all the information necessary to start a game
     */
    public void setUpGame(){
        int playerCount = getPlayerCount();
        if(playerCount < 2){
            throw new IllegalArgumentException("You need at least 2 players");
        }
        List<Player> players = new ArrayList<>(playerCount);
        for(int playerIndex = 1; playerIndex <= playerCount; playerIndex++){
            final Color playerColor = Color.values()[playerIndex % Color.values().length];
            players.add(new Player(getPlayerName(playerIndex), playerColor));
        }
        int[] boardDimensions = getBoardDimensions();
        game = new Game(boardDimensions[0], boardDimensions[1], players);
    }

    public void startGame(){
        if(game == null){
            throw new IllegalStateException("Game has not been set up!");
        }
        view.refresh();
        while (!game.hasEnded()) {
            final Line line = waitForLine(game.getCurrentPlayer());
            makeMove(line);
            view.refresh();
        }
        endGame(game.winners());
    }

    /**
     * Get the number of players that will be generated and therefore will play the current game.
     *
     * @return player count [1, ]
     */
    abstract int getPlayerCount();

    /**
     * Get the name to be assigned to the player with the passed number and color.
     *
     * @param playerNumber ordinal of the player being created
     * @return the string literal for the name to be assigned to the player being created
     */
    abstract String getPlayerName(int playerNumber);

    /**
     * Get the number of rows and columns of the game board being created, either asking for user input or
     * returning a fixed value
     *
     * @return an integer array containing two elements: int[2] { width, height }
     */
    abstract int[] getBoardDimensions();

    /**
     * Notify the UI to update the player visualization for the passed player instance (score counter and such).
     *
     * @param player the updated player instance
     */
    abstract void updatePlayer(Player player);

    /**
     * Wait for the UI to receive an event of a game turn being played by the user.
     *
     * @return the Line being played in the current turn by the playing Player (determined by the game instance)
     * @see Game#makeNextMove(Line)
     */
    abstract Line waitForLine(Player player);

    void makeMove(Line line){
        try {
            game.makeNextMove(new Line(line.p1().x(), line.p1().y(), line.p2().x(), line.p2().y()));
            game.updateScore();
        } catch (RuntimeException e) {
            view.displayIllegalMoveWarning(line.toString());
        }
    }

    /**
     * Notify the UI to terminate the current game specifying the passed player as the winner.
     *
     * @param winner the winning player
     */
    abstract void endGame(List<Player> winner);

    public PostGameIntent getPostGameIntent(){
        return view.promptForPostGameIntent();
    }
}

