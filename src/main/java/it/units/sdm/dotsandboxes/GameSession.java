package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.controllers.PostGameIntent;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedQuit;

import java.io.IOException;

public class GameSession{

    private final IGameController controller;

    GameSession(IGameController controller) {
        this.controller = controller;
    }

    public void begin() throws IOException {
        PostGameIntent intent;
        do {
            if(!controller.initialize()){
                throw new IOException("Could not initialize game");
            }
            if(!controller.setUpGame()){
                throw new IOException("Could not set up game");
            }
            try {
                controller.startGame();
            } catch (IOException e) {
                throw new IOException("Game controller could not start game.", e);
            } catch (UserHasRequestedQuit e) {
                break;
            }
            try {
                intent = controller.getPostGameIntent();
            } catch (IOException e) {
                throw new IOException("Problem acquiring the post game intentions.", e);
            }
        } while (intent == PostGameIntent.NEW_GAME);
    }
}
