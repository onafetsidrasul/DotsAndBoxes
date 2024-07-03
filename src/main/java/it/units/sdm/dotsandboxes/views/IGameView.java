package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Color;


import java.util.Map;
import java.util.SequencedCollection;

public interface IGameView {
    boolean init();

    default void updateUI(Board gameBoard, SequencedCollection<String> players, Map<String, Integer> scores, Map<String, Color> colors, String currentPlayer){}

    void promptForPostGameIntent();

    void displayMessage(String message);

    void displayIllegalActionWarning(String message);

    void promptForPlayerName(int playerNumber);

    void promptForBoardDimensions();

    void promptForNumberOfPlayers();

    void promptForAction(String currentPlayer);

    void displayWinners(SequencedCollection<String> winners);

    void promptForGamemode();
}
