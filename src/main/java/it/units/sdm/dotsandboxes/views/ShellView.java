package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.controllers.PostGameIntent;
import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ShellView implements IGameView { // devo trasferire la visualizzazione dal shellgamecontroller alla shellview

    @Override
    public boolean init() {
        //AnsiConsole.systemInstall();
        return true;
    }

    @Override
    public void updateUI(final Board gameBoard, final List<Player> players, final int[] scores, final Player currentPlayer) {
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

    private void printPlayers(List<Player> players, int[] scores) {
        System.out.println("--- PLAYERS ---");
        for (int i = 1; i <= players.size(); i++) {
            Player player = players.get(i - 1);
            System.out.println("Player " + i + " : " + player.name() + ", " + player.color());
            System.out.println("\tScore: " + scores[i - 1]);
        }
        System.out.println("---------------");
    }

    private void printCurrentPlayer(Player currentPlayer) {
        System.out.println("Current player: " + currentPlayer.name());
    }

    @Override
    public PostGameIntent promptForPostGameIntent() throws IOException {
        System.out.print("Do you wish to continue? [y/n] : ");
        String intent = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        intent = reader.readLine();
        return intent.equals("y") ? PostGameIntent.NEW_GAME : PostGameIntent.END_GAME;
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
    public String promptForPlayerName(int playerNumber) throws IOException {
        System.out.print("Insert name for player" + playerNumber + " : ");
        String name = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        name = reader.readLine();
        return name;
    }

    @Override
    public String[] promptForBoardDimensions() throws IOException {
        System.out.print("Insert board dimensions [ NxM ] : ");
        String dimensions = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        dimensions = reader.readLine();
        return dimensions.split("x");
    }

    @Override
    public String promptForNumberOfPlayers() throws IOException {
        System.out.print("Insert number of players : ");
        String playerNum = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        playerNum = reader.readLine();
        return playerNum;
    }

    @Override
    public String promptForMove(Player currentPlayer) throws IOException {
        System.out.print("Make your move [ x1, y1, x2, y2 ] : ");
        String move = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        move = reader.readLine();
        return move;
    }

    @Override
    public void displayWinners(List<Player> winners) {
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


}
