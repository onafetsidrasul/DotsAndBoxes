package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Color;
import it.units.sdm.dotsandboxes.core.ColoredLine;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;
import org.fusesource.jansi.Ansi;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedCollection;

import static org.fusesource.jansi.Ansi.*;

public class ShellView implements IGameView {

    @Override
    public boolean init() {
        //AnsiConsole.systemInstall();
        return true;
    }

    @Override
    public void updateUI(Board gameBoard, SequencedCollection<String> players, Map<String, Integer> scores, Map<String, Color> colors, String currentPlayer) {
        System.out.println(ansi().eraseScreen());
        printPlayers(List.copyOf(players), scores, colors);
        printBoard(gameBoard);
        printCurrentPlayer(currentPlayer);
    }

    private void printBoard(final Board gameBoard) {
        printUpperBorder(gameBoard.length());
        printBoardContents(gameBoard);
        printLowerBorder(gameBoard.length());
    }

    private void printUpperBorder(final int boardWidth) {
        printColumnNumbers(boardWidth);
        System.out.print("  ┏");
        printHorizontalBorder(boardWidth);
        System.out.println("┓");
    }

    private void printColumnNumbers(int width) {
        StringBuilder sb;
        sb = new StringBuilder("   ");
        for (int i = 0; i < width; i++) {
            sb.append(" ").append(i).append("  ");
        }
        System.out.println(sb);
    }

    private void printLowerBorder(final int boardWidth) {
        System.out.print("  ┗");
        printHorizontalBorder(boardWidth);
        System.out.println("┛");
    }

    private void printHorizontalBorder(final int boardWidth) {
        System.out.print("━".repeat(boardWidth * 4 - 1));
    }

    private void printBoardContents(final Board gameBoard) {
        for (int rowNumber = 0; rowNumber < gameBoard.height(); rowNumber++) {
            printRowContents(rowNumber, gameBoard);
        }

    }

    private void printRowContents(final int rowNumber, final Board gameBoard) {
        StringBuilder sb;
        sb = new StringBuilder();
        printRowLeftBorder(rowNumber);
        for (int j = 0; j < gameBoard.length(); j++) {
            sb.append(" ● ");
            if (j < gameBoard.length() - 1) {
                printHorizontalLineIfPresent(j, rowNumber, sb, gameBoard);
            }
        }
        System.out.print(sb);
        printRowBorder();
        System.out.println();
        if (rowNumber < gameBoard.height() - 1) {
            System.out.print("  ");
            printRowBorder();
            sb = new StringBuilder();
            for (int j = 0; j < gameBoard.length(); j++) {
                printVerticalLineIfPresent(j, rowNumber, sb, gameBoard);
            }
            System.out.print(sb);
            printRowBorder();
            System.out.println();
        }
    }

    private void printRowBorder() {
        System.out.print("┃");
    }

    private void printRowLeftBorder(final int rowNumber) {
        System.out.print(rowNumber + " ");
        printRowBorder();
    }

    private void printVerticalLineIfPresent(int j, int i, StringBuilder sb, Board gameBoard) {
        Line searched;
        try {
            searched = new Line(j, i, j, i + 1);
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
        Optional<ColoredLine> res = gameBoard.lines().stream().filter(coloredLine -> coloredLine.hasSameEndpointsAs(searched)).findFirst();
        if (res.isPresent()) {
            sb.append(res.get().color().format().format(" ‖ "));
        } else {
            sb.append("   ");
        }
        if (j < gameBoard.length() - 1) {
            sb.append(" ");
        }
    }

    private void printHorizontalLineIfPresent(int j, int i, StringBuilder sb, Board gameBoard) {
        Line searched;
        try {
            searched = new Line(j, i, j + 1, i);
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
        Optional<ColoredLine> res = gameBoard.lines().stream().filter(coloredLine -> coloredLine.hasSameEndpointsAs(searched)).findFirst();
        if (res.isPresent()) {
            sb.append(res.get().color().format().format("="));
        } else {
            sb.append(" ");
        }
    }

    private void printPlayers(List<String> players, Map<String, Integer> scores, Map<String, Color> colors) {
        System.out.println("--- PLAYERS ---");
        for (int i = 1; i <= players.size(); i++) {
            String player = players.get(i - 1);
            System.out.println(ansi().a("Player " + i + " : ").fg(Ansi.Color.valueOf(colors.get(player).name())).a(player).reset());
            System.out.println("\tScore: " + scores.get(player));
        }
        System.out.println("---------------");
    }

    private void printCurrentPlayer(String currentPlayer) {
        System.out.println("Current player: " + currentPlayer);
    }

    @Override
    public void promptForPostGameIntent() {
        System.out.print("Do you wish to play again? [y/n] : ");
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayIllegalActionWarning(String message) {
        System.out.println(message);
    }

    @Override
    public void promptForPlayerName(int playerNumber) {
        System.out.print(ansi().eraseScreen().a("Insert name for player" + playerNumber + " : "));
    }

    @Override
    public void promptForBoardDimensions() {
        System.out.print(ansi().eraseScreen().a("Insert board dimensions [ NxM ] : "));
    }

    @Override
    public void promptForNumberOfPlayers() {
        System.out.print(ansi().eraseScreen().a("Insert number of players : "));
    }

    @Override
    public void promptForAction(String currentPlayer) {
        System.out.println("Insert \"quit\" to quit the game");
        System.out.println("Insert \"save\" to save the game");
        System.out.print("Or make your move [ x1 y1 x2 y2 ] : ");
    }

    @Override
    public void displayWinners(SequencedCollection<String> winners) {
        System.out.println(ansi().eraseScreen());
        if (winners.size() > 1) {
            System.out.println("Game tied between the players: ");
            for (String winner : winners) {
                System.out.println(winner);
            }
        }
        if (winners.size() == 1) {
            System.out.println("Player " + winners.getFirst() + " won!");
        }
    }

    @Override
    public void promptForGamemode() {
        System.out.println(ansi().eraseScreen());
        System.out.println("Select the game mode");
        System.out.println("\t1 - Player vs. Player");
        System.out.println("\t2 - Player vs. CPU");
        System.out.print(" : ");
    }


}
