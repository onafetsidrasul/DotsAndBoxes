package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.controllers.PostGameIntent;
import it.units.sdm.dotsandboxes.core.Color;
import it.units.sdm.dotsandboxes.core.Game;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedQuit;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedSave;
import it.units.sdm.dotsandboxes.persistence.Savable;
import it.units.sdm.dotsandboxes.views.IGameView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GameSession implements Savable<GameSession> {

    private final IGameController controller;

    GameSession(IGameController controller) {
        this.controller = controller;
    }

    public record SavedGameSession(String gameControllerClassName, String data) {
    }

    public void start() throws IOException {
        PostGameIntent intent;
        do {
            controller.initialize();
            try {
                controller.setUpGame();
            } catch (IOException e) {
                throw new IOException("Game controller could not setup game.", e);
            }
            try {
                controller.startGame();
            } catch (IOException e) {
                throw new IOException("Game controller could not start game.", e);
            } catch(UserHasRequestedQuit e){
                break;
            } catch (UserHasRequestedSave e) {
                save();
            }
            try {
                intent = controller.getPostGameIntent();
            } catch (IOException e) {
                throw new IOException("Problem acquiring the post game intentions.", e);
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
            e.printStackTrace(System.out);
            return null;
        }
    }
}
