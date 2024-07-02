package it.units.sdm.dotsandboxes.controllers;

import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedQuit;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedSave;
import it.units.sdm.dotsandboxes.views.IGameView;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class ShellGameController extends IGameController {

    private final BufferedReader reader;

    public ShellGameController(IGameView view) {
        super(view);
        reader = new BufferedReader(new InputStreamReader(System.in));
    }


    @Override
    public void initialize() {
        view.init();
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

    public int[] getBoardDimensions() throws IOException {
        view.promptForBoardDimensions();
        try {
            return Arrays.stream(reader.readLine().split("x")).mapToInt(Integer::parseInt).toArray();
        } catch (NumberFormatException e) {
            System.err.println("Invalid input. Please enter valid integer coordinates");
            return null;
        }
    }

    public Line getAction(Player currentPlayer) throws IOException, InvalidInputException, UserHasRequestedSave, UserHasRequestedQuit {
        view.promptForAction(currentPlayer);
        Line candidate = null;
        do {
            String input = reader.readLine();
            if (input != null && !input.isEmpty()) {
                candidate = parseLineString(input);
            }
        } while (candidate == null);
        return candidate;
    }

    private Line parseLineString(String input) throws InvalidInputException, UserHasRequestedQuit, UserHasRequestedSave {
        if ("quit".equals(input)) {
            throw new UserHasRequestedQuit();
        }
        if("save".equals(input)) {
            throw new UserHasRequestedSave();
        }
        final List<String> coords = List.of(input.split(" "));
        if (coords.size() != 4) {
            view.displayIllegalActionWarning("Invalid input. Please enter four space-separated coordinates");
            return null;
        }
        int[] parsedCoords = coords.stream().mapToInt(Integer::parseInt).toArray();
        return parsedCoords == null ? null : new Line(parsedCoords[0], parsedCoords[1], parsedCoords[2], parsedCoords[3]);
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
        return switch (Integer.parseInt(reader.readLine())) {
            case 1 -> Gamemode.PVP;
            case 2 -> Gamemode.PVE;
            default -> throw new IllegalArgumentException("Unexpected value: " + reader.readLine());
        };
    }
}
