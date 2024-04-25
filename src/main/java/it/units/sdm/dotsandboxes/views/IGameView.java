package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;

import java.util.List;

public interface IGameView {
    boolean init();

    default void updateUI(Board gameBoard, List<Player> players, int[] scores, Player currentPlayer){};

    void promptForPostGameIntent();

    void displayIllegalMoveWarning(Line illegalLine);

    void displayIllegalActionWarning(String message);

    void promptForPlayerName(int playerNumber);

    void promptForBoardDimensions();

    void promptForNumberOfPlayers();

    void promptForMove(Player currentPlayer);

    void displayWinners(List<Player> winners);

    void promptForGamemode();
}
