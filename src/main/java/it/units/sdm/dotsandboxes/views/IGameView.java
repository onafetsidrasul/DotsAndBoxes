package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.core.Board;

public interface IGameView {
    void init(Board gameBoard);

    void refresh();
}
