package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.controllers.PostGameIntent;

import java.io.IOException;

public class GameSession {

    private final IGameController controller;

    GameSession(IGameController controller) {
        this.controller = controller;
    }

    public void start() {
        PostGameIntent intent = PostGameIntent.END_GAME;
        do {
            if (!controller.initialize()) {
                throw new RuntimeException("Game controller could not initialize!");
            }
            try {
                controller.setUpGame();
            } catch (IOException e) {
                System.err.println("Game controller could not setup game!");
            }
            try {
                controller.startGame();
            } catch (IOException e) {
                System.err.println("Game controller could not start game!");
            }
            try {
                intent = controller.getPostGameIntent();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } while (intent == PostGameIntent.NEW_GAME);
    }


}
