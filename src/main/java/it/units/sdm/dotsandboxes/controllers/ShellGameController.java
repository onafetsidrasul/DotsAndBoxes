package it.units.sdm.dotsandboxes.controllers;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import it.units.sdm.dotsandboxes.BoardPrinter;
import it.units.sdm.dotsandboxes.InputHandler;
import it.units.sdm.dotsandboxes.PromptForPlayerName;
import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;
import org.fusesource.jansi.AnsiConsole;

import java.util.List;

public class ShellGameController implements IGameController {

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
        return inputHandler.waitForLine(currentPlayer);
    }

    @Override
    public void endGame(List<Player> winner) {
        System.out.println("The winner is " + winner);
    }
}
