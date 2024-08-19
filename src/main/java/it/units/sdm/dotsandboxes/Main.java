package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.views.ShellView;
import it.units.sdm.dotsandboxes.views.SwingView;
import it.units.sdm.dotsandboxes.views.TextView;

public class Main {

    public static void main(String... args) {
        if (args.length == 1) {
            final GameSession session = switch (args[0]) {
                case "text" -> new GameSession(new IGameController(new TextView()));
                case "tui" -> new GameSession(new IGameController(new ShellView()));
                case "gui" -> new GameSession(new IGameController(new SwingView()));
                default -> {
                    System.out.println("Invalid game mode!ì.");
                    yield null;
                }
            };
            try {
                session.begin();
            } catch (Exception e) {
                e.printStackTrace(System.out);
            } finally {
                System.out.println("Game has terminated.");
            }
        } else {
            System.out.println("Usage: \n java -jar DotsAndBoxes.jar text|tui|gui");
        }

    }
}
