package it.units.sdm.dotsandboxes.controllers;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.InputResult;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import it.units.sdm.dotsandboxes.PromptForPlayerName;
import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Color;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ShellGameController implements IGameController {

    private ConsolePrompt prompt;
    private PromptForPlayerName playerNamePrompt;

    @Override
    public boolean initialize() {
        AnsiConsole.systemInstall();
        prompt = new ConsolePrompt();
        playerNamePrompt = new PromptForPlayerName(prompt);
        return true;
    }

    @Override
    public int getPlayerCount() {
        return 2;
    }

    @Override
    public String getPlayerName(int playerNumber, Color color) {
        return playerNamePrompt.getPlayerName(playerNumber);
    }

    @Override
    public int[] getBoardDimensions() {
        return new int[] { 6, 6 };
    }

    @Override
    public void updateBoard(Board board) {
        final int[] dimensions = getBoardDimensions();
        final int width = dimensions[0], height = dimensions[1];
        StringBuilder sb;
        Line searched;

        sb = new StringBuilder("   ");
        for (int j = 0; j < width; j++) {
            sb.append(" ").append(j).append("  ");
        }
        System.out.println(sb);

        System.out.println("  ┏" + "━".repeat(width * 4 - 1) + "┓");
        for (int i = 0; i < height; i++) {
            sb = new StringBuilder(i + " ┃");
            for (int j = 0; j < width; j++) {
                sb.append(" ● ");
                if (j < width - 1) {
                    searched = new Line(j, i, j + 1, i);
                    if (board.getLines().containsKey(searched.hashCode())) {
                        sb.append(board.getLines().get(
                                searched.hashCode()).color().getFormat().format("="));
                    } else {
                        sb.append(" ");
                    }
                }
            }
            System.out.println(sb + "┃");

            if (i < height - 1) {
                sb = new StringBuilder("  ┃");
                for (int j = 0; j < width; j++) {
                    searched = new Line(j, i, j, i + 1);
                    if (board.getLines().containsKey(searched.hashCode())) {
                        sb.append(board.getLines().get(
                                searched.hashCode()).color().getFormat().format(" ‖ "));
                    } else {
                        sb.append("   ");
                    }
                    if (j < width - 1) {
                        sb.append(" ");
                    }
                }
                System.out.println(sb + "┃");
            }
        }
        System.out.println("  ┗" + "━".repeat(width * 4 - 1) + "┛");
    }

    @Override
    public void updatePlayer(Player player) {
        System.out.println(player);
    }

    @Override
    public Line waitForLine(Player currentPlayer) {
        Line candidate = null;
        String input;
        do {
            String promptName = "move";
            try {
                InputResult ir = (InputResult) prompt.prompt(
                        prompt.getPromptBuilder().createInputPrompt()
                                .name(promptName)
                                .message("Move x1 y1 x2 y2")
                                .addPrompt().build()
                ).get(promptName);
                input = ir.getInput();
            } catch (IOException e) {
                continue;
            }

            if (input == null || input.isEmpty()) {
                continue;
            }
            final String[] coords = input.split(" ");
            if (coords.length < 4) {
                continue;
            }
            int x1, y1, x2, y2;
            try {
                x1 = Integer.parseInt(coords[0]);
                y1 = Integer.parseInt(coords[1]);
                x2 = Integer.parseInt(coords[2]);
                y2 = Integer.parseInt(coords[3]);
            } catch (NumberFormatException e) {
                continue;
            }
            candidate = new Line(x1, y1, x2, y2);
        } while (candidate == null);
        return candidate;
    }

    @Override
    public void endGame(List<Player> winner) {
        System.out.println("The winner is " + winner);
    }
}
