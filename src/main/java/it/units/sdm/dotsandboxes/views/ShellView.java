package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Color;
import it.units.sdm.dotsandboxes.core.ColoredLine;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedCollection;

import static org.fusesource.jansi.Ansi.*;

public class ShellView implements IGameView {

    private final PrintStream out;

    public ShellView(PrintStream out) {
        this.out = out;
    }

    @Override
    public boolean init() {
        AnsiConsole.systemInstall();
        return true;
    }

    @Override
    public void updateUI(Board gameBoard, SequencedCollection<String> players, Map<String, Integer> scores, Map<String, Color> colors, String currentPlayer) {
        eraseScreen();
        printPlayers(List.copyOf(players), scores, colors);
        printBoard(gameBoard);
        printCurrentPlayer(currentPlayer, colors.get(currentPlayer));
    }

    private void printBoard(final Board gameBoard) {
        printUpperBorder(gameBoard.length());
        printBoardContents(gameBoard);
        printLowerBorder(gameBoard.length());
    }

    private void printUpperBorder(final int boardWidth) {
        printColumnNumbers(boardWidth);
        out.print("  ┏");
        printHorizontalBorder(boardWidth);
        out.println("┓");
    }

    private void printColumnNumbers(int width) {
        StringBuilder sb;
        sb = new StringBuilder("   ");
        for (int i = 0; i < width; i++) {
            sb.append(" ").append(i).append("  ");
        }
        out.println(sb);
    }

    private void printLowerBorder(final int boardWidth) {
        out.print("  ┗");
        printHorizontalBorder(boardWidth);
        out.println("┛");
    }

    private void printHorizontalBorder(final int boardWidth) {
        out.print("━".repeat(boardWidth * 4 - 1));
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
        out.print(sb);
        printRowBorder();
        out.println();
        if (rowNumber < gameBoard.height() - 1) {
            out.print("  ");
            printRowBorder();
            sb = new StringBuilder();
            for (int j = 0; j < gameBoard.length(); j++) {
                printVerticalLineIfPresent(j, rowNumber, sb, gameBoard);
            }
            out.print(sb);
            printRowBorder();
            out.println();
        }
    }

    private void printRowBorder() {
        out.print("┃");
    }

    private void printRowLeftBorder(final int rowNumber) {
        out.print(rowNumber + " ");
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
        out.println("--- PLAYERS ---");
        for (int i = 1; i <= players.size(); i++) {
            String player = players.get(i - 1);
            out.println(ansi().a("Player " + i + " : ").fg(Ansi.Color.valueOf(colors.get(player).name())).a(player).reset());
            out.println("\tScore: " + scores.get(player));
        }
        out.println("---------------");
    }

    private void printCurrentPlayer(String currentPlayer, Color currentPlayerColor) {
        out.println(ansi().a("Current player: ").fg(Ansi.Color.valueOf(currentPlayerColor.name())).a(currentPlayer).reset());
    }

    @Override
    public void promptForPostGameIntent() {
        displayPrompt("Do you wish to play again? [y/n] : ");
    }

    @Override
    public void displayMessage(String message) {
        out.println(ansi().fg(Ansi.Color.GREEN).a(message).reset());
    }

    private void displayPrompt(String message){
        out.print(ansi().fg(Ansi.Color.YELLOW).a(message).reset());
    }

    @Override
    public void displayIllegalActionWarning(String message) {
        out.print(ansi().fg(Ansi.Color.RED).a(message).a(". Press Enter to continue :").reset());

    }

    @Override
    public void promptForPlayerName(int playerNumber) {
        eraseScreen();
        displayPrompt("Insert name for player" + playerNumber + " : ");
    }

    @Override
    public void promptForBoardDimensions() {
        eraseScreen();
        displayPrompt("Insert board dimensions [ NxM ] : ");
    }

    @Override
    public void promptForNumberOfPlayers() {
        eraseScreen();
        displayPrompt("Insert number of players : ");
    }

    @Override
    public void promptForAction(String currentPlayer) {
        displayPrompt("Insert \"quit\" to quit the game\n");
        displayPrompt("Insert \"save\" to save the game\n");
        displayPrompt("Or make your move [ x1 y1 x2 y2 ] : ");
    }

    @Override
    public void displayWinners(SequencedCollection<String> winners) {
        eraseScreen();
        if (winners.size() > 1) {
            out.println("Game tied between the players: ");
            for (String winner : winners) {
                out.println(winner);
            }
        }
        if (winners.size() == 1) {
            out.println("Player " + winners.getFirst() + " won!");
        }
    }

    @Override
    public void promptForGamemode() {
        eraseScreen();
        out.println("Select the game mode");
        out.println("\t1 - Player vs. Player");
        out.println("\t2 - Player vs. CPU");
        out.print(" : ");
    }

    private void eraseScreen(){
        out.println(ansi().eraseScreen());
    }


}
