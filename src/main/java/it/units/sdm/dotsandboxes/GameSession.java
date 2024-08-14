package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.controllers.PostGameIntent;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedQuit;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedSave;
import it.units.sdm.dotsandboxes.persistence.Savable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GameSession implements Savable<GameSession> {

    private final IGameController controller;

    GameSession(IGameController controller) {
        this.controller = controller;
    }

    public record SavedGameSession(String gameControllerClassName, String data) {
    }

    public void begin() throws IOException {
        PostGameIntent intent;
        do {
            if (!controller.initialize()) {
                throw new IOException("Could not initialize game");
            }
            if (!controller.setUpGame()) {
                throw new IOException("Could not set up game");
            }
            try {
                controller.startGame();
            } catch (IOException e) {
                throw new IOException("Game controller could not start game.", e);
            } catch (UserHasRequestedQuit e) {
                break;
            } catch (UserHasRequestedSave e) {
                serialized();
            }
            try {
                intent = controller.getPostGameIntent();
            } catch (IOException e) {
                throw new IOException("Problem acquiring the post game intentions.", e);
            }
        } while (intent == PostGameIntent.NEW_GAME);
    }

    @Override
    public byte[] serialized() {
        final SavedGameSession savedGameSession = new SavedGameSession(
                controller.getClass().getCanonicalName(), encoder.encodeToString(controller.serialized()));
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
