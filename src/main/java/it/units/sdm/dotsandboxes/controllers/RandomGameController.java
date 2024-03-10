package it.units.sdm.dotsandboxes.controllers;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import it.units.sdm.dotsandboxes.BoardPrinter;
import it.units.sdm.dotsandboxes.InputHandler;
import it.units.sdm.dotsandboxes.PromptForPlayerName;
import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;
import it.units.sdm.dotsandboxes.core.Point;
import org.fusesource.jansi.AnsiConsole;

import java.util.*;

public class RandomGameController implements IGameController {

    private final Set<Line> drawnLines = new HashSet<>();
    private InputHandler inputHandler;
    private PromptForPlayerName playerNamePrompt;

    @Override
    public boolean initialize() {
        AnsiConsole.systemInstall();
        ConsolePrompt prompt = new ConsolePrompt();
        inputHandler = new InputHandler(prompt);
        playerNamePrompt = new PromptForPlayerName(prompt);
        return true;
    }

    @Override
    public int getPlayerCount() {
        return 2;
    }

    @Override
    public String getPlayerName(int playerNumber) {
        if (playerNumber == 2) {
            return "Computer";
        }
        return playerNamePrompt.getPlayerName(playerNumber);
    }

    @Override
    public int[] getBoardDimensions() {
        return new int[] { 5, 5 };
    }

    @Override
    public void updateBoard(Board board) {
        final int[] dimensions = getBoardDimensions();
        BoardPrinter.printBoard(board, dimensions);
    }

    @Override
    public void updatePlayer(Player player) {
        System.out.println(player);
    }

    @Override
    public Line waitForLine(Player currentPlayer) {
        if (currentPlayer.getName().equals("Computer")) {
            int[] dims = getBoardDimensions();
            Line candidate;
            do { candidate = randomLine(dims); }
            while (drawnLines.contains(candidate));
            drawnLines.add(candidate);
            return candidate;
        }else{
            Line candidate= inputHandler.waitForLine(currentPlayer);
            drawnLines.add(candidate);
            return candidate;
        }
    }

    private static Line randomLine(int[] dims) {
        Line candidate;
        Point p1 = getFirstPoint(dims);
        Point p2;
        do {p2 = getSecondPoint(p1);
        } while (!isValidPoint(p2.x(), p2.y(), dims));
        candidate = new Line(p1.x(), p1.y(), p2.x(), p2.y());
        return candidate;
    }

    private static Point getFirstPoint(int[] dims) {
        int x1 = (int) Math.floor(Math.random() * dims[0]);
        int y1 = (int) Math.floor(Math.random() * dims[1]);
        return new Point(x1, y1);
    }

    private static Point getSecondPoint(Point p){
        if (Math.random()>=0.5){
            return new Point(p.x(),p.y()+(Math.random() >= 0.5 ? 1 : -1));
        }else{
            return new Point(p.x()+(Math.random() >= 0.5 ? 1 : -1),p.y());
        }
    }

    private static boolean isValidPoint(int x, int y, int[] dims) {
        return x >= 0 && x < dims[0] && y >= 0 && y < dims[1];
    }

    @Override
    public void endGame(List<Player> winner) {
        System.out.println("The winner is " + winner);
    }
}