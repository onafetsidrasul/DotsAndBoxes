package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Player;

import java.util.List;

public interface IGameView {
    boolean init();

    default void updateUI(Board gameBoard, List<Player> players, List<Integer> scores, Player currentPlayer){}

    void promptForPostGameIntent();

    void displayMessage(String message);

    void displayIllegalActionWarning(String message);

    void promptForPlayerName(int playerNumber);

    void promptForBoardDimensions();

    void promptForNumberOfPlayers();

    void promptForAction(Player currentPlayer);

    void displayWinners(List<Player> winners);

    void promptForGamemode();
}
