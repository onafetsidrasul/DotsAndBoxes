package it.units.sdm.dotsandboxes.controllers;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.InputResult;
import it.units.sdm.dotsandboxes.views.IGameView;
import it.units.sdm.dotsandboxes.views.ShellView;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.util.List;

public class ShellGameController extends IGameController {
    private ConsolePrompt prompt;
    private ShellView view;

    public ShellGameController(IGameView view) {
        super(view);
    }

    @Override
    public boolean initialize() {
        AnsiConsole.systemInstall();
        prompt = new ConsolePrompt();
        view = new ShellView();
        return true;
    }
    @Override
    public int getPlayerCount() {return 2;}

    @Override
    public String getPlayerName(int playerNumber) {
        String name = "";
        do {
            String promptName = "name" + playerNumber;
            try {
                InputResult ir = getInputResult(playerNumber, promptName);
                name = ir.getInput();
            } catch (IOException ignored) {}
        } while (name == null || name.isEmpty());
        return name;
    }

    private InputResult getInputResult(int playerNumber, String promptName) throws IOException {
        return (InputResult) prompt.prompt(
                prompt.getPromptBuilder().createInputPrompt()
                        .name(promptName)
                        .defaultValue("Player " + playerNumber)
                        .message("Name for player #" + playerNumber)
                        .addPrompt().build()
        ).get(promptName);
    }

    @Override
    public int[] getBoardDimensions() {
        return new int[] { 5, 5 };
    }

    @Override
    public void updatePlayer(Player player) {
        System.out.println(player);
    }

    @Override
    public Line waitForLine(Player currentPlayer) {
        Line candidate = null;
        do {
            String input = getValidatedInput(currentPlayer);
            if (input != null && !input.isEmpty()) {
                candidate = CreateLine(input);
            }
        } while (candidate == null);
        return candidate;
    }

    private String getValidatedInput(Player currentPlayer) {
        String input = null;
        String promptName = "move";
        try {
            input = promptForMoveInput(currentPlayer, promptName);
        } catch (IOException | RuntimeException e) {
            System.err.println("An error occurred while prompting for input. Please try again");
        }
        return input;
    }

    private String promptForMoveInput(Player currentPlayer, String promptName) throws IOException {
        String input;
        InputResult ir = (InputResult) prompt.prompt(
                prompt.getPromptBuilder().createInputPrompt()
                        .name(promptName)
                        .message(currentPlayer.name()+", make a move x1 y1 x2 y2")
                        .addPrompt().build()
        ).get(promptName);
        input = ir.getInput();
        return input;
    }

    private Line CreateLine(String input) {
        final String[] coords = input.split(" ");
        if (coords.length != 4) {
            System.err.println("Invalid input. Please enter four space-separated coordinates");
            return null;
        }
        int[] parsedCoords = parseCoordinates(coords);
        if (parsedCoords == null) {
            return null;
        }
        return new Line(parsedCoords[0], parsedCoords[1], parsedCoords[2], parsedCoords[3]);
    }

    private int[] parseCoordinates(String[] coords) {
        int x1, y1, x2, y2;
        try {
            x1 = Integer.parseInt(coords[0]);
            y1 = Integer.parseInt(coords[1]);
            x2 = Integer.parseInt(coords[2]);
            y2 = Integer.parseInt(coords[3]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid input. Please enter valid integer coordinates");
            return null;
        }
        return new int[]{x1, y1, x2, y2};
    }

    @Override
    public void endGame(List<Player> winner) {
        System.out.println("The winner is " + winner);
    }
}
