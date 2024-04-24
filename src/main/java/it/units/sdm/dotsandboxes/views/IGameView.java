package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.controllers.PostGameIntent;
import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;

import java.io.IOException;
import java.util.List;

public interface IGameView {
    boolean init();

    default void updateUI(Board gameBoard, List<Player> players, int[] scores, Player currentPlayer){};

    PostGameIntent promptForPostGameIntent() throws IOException;

    void displayIllegalMoveWarning(Line illegalLine);

    void displayIllegalActionWarning(String message);

    String promptForPlayerName(int playerNumber) throws IOException;

    String[] promptForBoardDimensions() throws IOException;

    String promptForNumberOfPlayers() throws IOException;

    String promptForMove(Player currentPlayer) throws IOException;

    void displayWinners(List<Player> winners);
}
