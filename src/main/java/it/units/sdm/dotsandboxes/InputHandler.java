package it.units.sdm.dotsandboxes;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.InputResult;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;

import java.io.IOException;

public class InputHandler {
    private final ConsolePrompt prompt;

    public InputHandler(ConsolePrompt prompt) {
        this.prompt = prompt;
    }

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
                        .message(currentPlayer.getName()+", make a move x1 y1 x2 y2")
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

}
