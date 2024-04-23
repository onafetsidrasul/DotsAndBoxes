package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.controllers.PostGameIntent;
import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Line;

public interface IGameView {
    void init(Board gameBoard);

    void refresh();

    PostGameIntent promptForPostGameIntent();

    void displayIllegalMoveWarning(String lineString);
}
