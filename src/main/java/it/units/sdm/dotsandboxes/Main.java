package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.TerminalGameController;
import it.units.sdm.dotsandboxes.persistence.IGameSaver;
import it.units.sdm.dotsandboxes.persistence.JsonGameSaver;
import it.units.sdm.dotsandboxes.views.ShellView;
import it.units.sdm.dotsandboxes.views.TextView;

public class Main {

    private static final IGameSaver<GameSession> saver = new JsonGameSaver();

    public static void main(String... args) {
        if (args.length == 1) {
            final GameSession session = switch (args[0]) {
                case "tui" -> new GameSession(new TerminalGameController(new ShellView()));
                //case "gui" -> new GameSession(new SwingGameController(new SwingView()));
                default -> (GameSession) saver.restoreFromFile(args[0]);
            };
            try {
                session.start();
            } catch (Exception e) {
                e.printStackTrace(System.out);
            } finally {
                System.out.println("Game has terminated.");
            }
        } else {
            System.out.println("Usage: \n java -jar DotsAndBoxes.jar <gamemode> \n java -jar DotsAndBoxes.jar <gamesave>");
        }

    }
}
