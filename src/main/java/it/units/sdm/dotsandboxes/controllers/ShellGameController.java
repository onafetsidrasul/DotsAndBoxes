package it.units.sdm.dotsandboxes.controllers;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.InputResult;
import it.units.sdm.dotsandboxes.views.IGameView;
import it.units.sdm.dotsandboxes.views.ShellView;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;

public class ShellGameController extends IGameController {

    private final BufferedReader reader;

    public ShellGameController(IGameView view) {
        super(view);
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public boolean initialize() {
        return view.init();
    }

    @Override
    public int getPlayerCount() throws IOException {
        view.promptForNumberOfPlayers();
        return Integer.parseInt(reader.readLine());
    }

    @Override
    public String getPlayerName(int playerNumber) throws IOException {
        view.promptForPlayerName(playerNumber);
        return reader.readLine();
    }

    @Override
    public int[] getBoardDimensions() throws IOException {
        view.promptForBoardDimensions();
        try {
            return Arrays.stream(reader.readLine().split("x")).mapToInt(Integer::parseInt).toArray();
        } catch (NumberFormatException e) {
            System.err.println("Invalid input. Please enter valid integer coordinates");
            return null;
        }
    }

    @Override
    public Line waitForLine(Player currentPlayer) {
        view.promptForMove(currentPlayer);
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
        try {
            input = reader.readLine();
        } catch (IOException | RuntimeException e) {
            System.err.println("An error occurred while prompting for input. Please try again");
        }
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
    public void endGame(List<Player> winners) {
        view.displayWinners(winners);
    }

    @Override
    public PostGameIntent getPostGameIntent() throws IOException {
        view.promptForPostGameIntent();
        return reader.readLine().equals("y") ? PostGameIntent.NEW_GAME : PostGameIntent.END_GAME;
    }

    @Override
    public Gamemode getGamemode() throws IOException {
        view.promptForGamemode();
        return switch (Integer.parseInt(reader.readLine())){
            case 1 -> Gamemode.PVP;
            case 2 -> Gamemode.PVE;
            default -> throw new IllegalArgumentException("Unexpected value: " + reader.readLine());
        };
    }
}
