package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.ShellGameController;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedQuit;
import it.units.sdm.dotsandboxes.persistence.IGameSaver;
import it.units.sdm.dotsandboxes.persistence.JsonGameSaver;
import it.units.sdm.dotsandboxes.views.ShellView;

import java.util.Arrays;
import java.util.Date;

public class Main {

    private static final IGameSaver<GameSession> saver = new JsonGameSaver();

    public static void main(String[] args) {
        final GameSession session;
        if (args.length > 0) {
            session = (GameSession) saver.restoreFromFile(args[0]);
        } else {
            session = new GameSession(new ShellGameController(), new ShellView());
        }
        try {
            session.start();
        } catch (UserHasRequestedQuit e) {
            final String filename;
            if ((filename = saver.save(session)) != null) {
                System.out.println("Saved game session in " + filename);
            }
            System.out.println("Bye!");
        }
    }
}
