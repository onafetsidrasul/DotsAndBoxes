package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.controllers.PostGameIntent;
import it.units.sdm.dotsandboxes.core.Color;
import it.units.sdm.dotsandboxes.core.Game;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;
import it.units.sdm.dotsandboxes.views.IGameView;

import java.util.ArrayList;
import java.util.List;

public class GameSession {

    private final IGameController controller;

    GameSession(IGameController controller) {
        this.controller = controller;
    }

    public void start() {
        do {
            if (!controller.initialize()) {
                throw new RuntimeException("Game controller could not initialize!");
            }
            controller.setUpGame();
            controller.startGame();
        } while (controller.getPostGameIntent() == PostGameIntent.NEW_GAME);
    }


}
