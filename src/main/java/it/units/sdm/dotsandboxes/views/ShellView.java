package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;
import org.fusesource.jansi.Ansi;

import java.util.List;

import static org.fusesource.jansi.Ansi.*;

public class ShellView implements IGameView {

    @Override
    public boolean init() {
        //AnsiConsole.systemInstall();
        return true;
    }

    @Override
    public void updateUI(final Board gameBoard, final List<Player> players, final List<Integer> scores, final Player currentPlayer) {
        System.out.println(ansi().eraseScreen());
        printPlayers(players, scores);
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
        searched = new Line(j, i, j, i + 1);
        if (gameBoard.getLines().containsKey(searched.hashCode())) {
            sb.append(gameBoard.getLines().get(
                    searched.hashCode()).color().getFormat().format(" ‖ "));
        } else {
            sb.append("   ");
        }
        if (j < gameBoard.length() - 1) {
            sb.append(" ");
        }
    }

    private void printHorizontalLineIfPresent(int j, int i, StringBuilder sb, Board gameBoard) {
        Line searched;
        searched = new Line(j, i, j + 1, i);
        if (gameBoard.getLines().containsKey(searched.hashCode())) {
            sb.append(gameBoard.getLines().get(
                    searched.hashCode()).color().getFormat().format("="));
        } else {
            sb.append(" ");
        }
    }

    private void printPlayers(List<Player> players, List<Integer> scores) {
        System.out.println("--- PLAYERS ---");
        for (int i = 1; i <= players.size(); i++) {
            Player player = players.get(i - 1);
            System.out.println(ansi().a("Player " + i + " : ").fg(Ansi.Color.valueOf(player.color().name())).a(player.name()).reset());
            System.out.println("\tScore: " + scores.get(i - 1));
        }
        System.out.println("---------------");
    }

    private void printCurrentPlayer(Player currentPlayer) {
        System.out.println("Current player: " + currentPlayer.name());
    }

    @Override
    public void promptForPostGameIntent() {
        System.out.print("Do you wish to play again? [y/n] : ");
    }

    @Override
    public void displayIllegalMoveWarning(Line illegalLine) {
        System.out.println("Line " + illegalLine + " is not allowed, try again");
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
    public void promptForMove(Player currentPlayer) {
        System.out.print("Make your move [ x1, y1, x2, y2 ] : ");
    }

    @Override
    public void displayWinners(List<Player> winners) {
        System.out.println(ansi().eraseScreen());
        if (winners.size() > 1) {
            System.out.println("Game tied between the players: ");
            for (Player winner : winners) {
                System.out.println(winner.name());
            }
        }
        if (winners.size() == 1) {
            System.out.println("Player " + winners.getFirst().name() + " won!");
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
