package it.units.sdm.dotsandboxes.controllers;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import it.units.sdm.dotsandboxes.BoardPrinter;
import it.units.sdm.dotsandboxes.InputHandler;
import it.units.sdm.dotsandboxes.PromptForPlayerName;
import it.units.sdm.dotsandboxes.RandomLine;
import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;
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
            do{ candidate= normalize(RandomLine.randomCandidate(dims));
            } while (drawnLines.contains(candidate));
            drawnLines.add(candidate);
            return candidate;
        }else{
            Line candidate= normalize(inputHandler.waitForLine(currentPlayer));
            drawnLines.add(candidate);
            return candidate;
        }
    }

    private static Line normalize(Line candidate) {
        if (Board.isNotNormalized(candidate))
            candidate =Board.normalizer(candidate);
        return candidate;
    }

    @Override
    public void endGame(List<Player> winner) {
        System.out.println("The winner is " + winner);
    }
}