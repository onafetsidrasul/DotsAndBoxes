package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.controllers.PostGameIntent;
import it.units.sdm.dotsandboxes.core.Color;
import it.units.sdm.dotsandboxes.core.Game;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;
import it.units.sdm.dotsandboxes.persistence.Savable;
import it.units.sdm.dotsandboxes.views.IGameView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GameSession implements Savable<GameSession> {

    private IGameController controller;

    GameSession(IGameController controller) {
        this.controller = controller;
    }

    public record SavedGameSession(String gameControllerClassName, String data) {}

    public GameSession() {
    }

    public void start() {
        PostGameIntent intent = PostGameIntent.END_GAME;
        do {
            if (!controller.initialize()) {
                throw new RuntimeException("Game controller could not initialize!");
            }
            if (!controller.isSetupDone()) {
                try {
                    controller.setUpGame();
                } catch (IOException e) {
                    System.err.println("Game controller could not setup game!");
                }
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

    @Override
    public byte[] save() {
        final SavedGameSession savedGameSession = new SavedGameSession(
                controller.getClass().getCanonicalName(), encoder.encodeToString(controller.save()));
        return gson.toJson(savedGameSession).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public GameSession restore(byte[] data) {
        final SavedGameSession savedGameSession =
                gson.fromJson(new String(data), SavedGameSession.class);
        final IGameController controller;
        try {
            controller = (IGameController) Class.forName(savedGameSession.gameControllerClassName)
                    .getConstructor()
                    .newInstance();
            return new GameSession(controller.restore(decoder.decode(savedGameSession.data)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
