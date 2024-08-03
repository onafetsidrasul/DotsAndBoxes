package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.core.*;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;


public class TextView extends IGameView {

    private final PrintStream out;
    private final BufferedReader in;

    public TextView() {
        this.out = System.out;
        this.in = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    protected boolean finishInit() {
        return true;
    }


    @Override
    protected boolean finishConfigure() {
        return true;
    }

    @Override
    protected void displayGameUI() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        do {
            try {
                controllerReference.refreshUISem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                controllerReference.gameOverCheckSem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (controllerReference.gameIsOver) {
                break;
            }
            eraseScreen();
            printPlayers(gameStateReference.players, gameStateReference.scoreBoard);
            printBoard(gameStateReference.board);
            printCurrentPlayer(gameStateReference.currentPlayer());
            isRefreshingUISem.release();
            controllerReference.input = promptForAction();
            controllerReference.inputHasBeenReceivedSem.release();
        } while (true);
    }

    protected void printBoard(final Board gameBoard) {
        printUpperBorder(gameBoard.width());
        printBoardContents(gameBoard);
        printLowerBorder(gameBoard.width());
    }

    protected void printUpperBorder(final int boardWidth) {
        printColumnNumbers(boardWidth);
        out.print("  ┏");
        printHorizontalBorder(boardWidth);
        out.println("┓");
    }

    protected void printColumnNumbers(final int boardWidth) {
        StringBuilder sb;
        sb = new StringBuilder("   ");
        for (int i = 0; i < boardWidth; i++) {
            sb.append(" ").append(i).append("  ");
        }
        out.println(sb);
    }

    protected void printLowerBorder(final int boardWidth) {
        out.print("  ┗");
        printHorizontalBorder(boardWidth);
        out.println("┛");
    }

    protected void printHorizontalBorder(final int boardWidth) {
        out.print("━".repeat(boardWidth * 4 - 1));
    }

    protected void printBoardContents(final Board gameBoard) {
        for (int rowNumber = 0; rowNumber < gameBoard.height(); rowNumber++) {
            printRowContents(rowNumber, gameBoard);
        }

    }

    protected void printRowContents(final int rowNumber, final Board gameBoard) {
        StringBuilder sb;
        sb = new StringBuilder();
        printRowLeftBorder(rowNumber);
        for (int j = 0; j < gameBoard.width(); j++) {
            sb.append(" ● ");
            if (j < gameBoard.width() - 1) {
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
            for (int j = 0; j < gameBoard.width(); j++) {
                printVerticalLineIfPresent(j, rowNumber, sb, gameBoard);
            }
            out.print(sb);
            printRowBorder();
            out.println();
        }
    }

    protected void printRowBorder() {
        out.print("┃");
    }

    protected void printRowLeftBorder(final int rowNumber) {
        out.print(rowNumber + " ");
        printRowBorder();
    }

    protected void printVerticalLineIfPresent(int j, int i, StringBuilder sb, Board gameBoard) {
        Line searched;
        try {
            searched = new Line(j, i, j, i + 1);
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
        synchronized (gameBoard.lines()) {
            Optional<ColoredLine> res = gameBoard.lines().stream().filter(coloredLine -> coloredLine.hasSameEndpointsAs(searched)).findFirst();
            if (res.isPresent()) {
                sb.append(" ‖ ");
            } else {
                sb.append("   ");
            }
        }
        if (j < gameBoard.width() - 1) {
            sb.append(" ");
        }
    }

    protected void printHorizontalLineIfPresent(int j, int i, StringBuilder sb, Board gameBoard) {
        Line searched;
        try {
            searched = new Line(j, i, j + 1, i);
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
        synchronized (gameBoard.lines()) {
            Optional<ColoredLine> res = gameBoard.lines().stream().filter(coloredLine -> coloredLine.hasSameEndpointsAs(searched)).findFirst();
            if (res.isPresent()) {
                sb.append("=");
            } else {
                sb.append(" ");
            }
        }

    }

    protected void printPlayers(List<String> players, Map<String, Integer> scores) {
        out.println("--- PLAYERS ---");
        synchronized (gameStateReference.scoreBoard) {
            for (int i = 1; i <= players.size(); i++) {
                String player = players.get(i - 1);
                out.println("Player " + i + " : " + player);
                out.println("\tScore: " + scores.get(player));
            }
        }
        out.println("---------------");
    }

    protected void printCurrentPlayer(String currentPlayer) {
        out.println("Current player: " + currentPlayer);
    }

    @Override
    public String promptForPostGameIntent() {
        displayPrompt("Do you wish to play again? [y/n] : ");
        String choice;
        try {
            choice = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return switch (choice) {
            case "y" -> "NEW";
            case "n" -> "END";
            default -> null;
        };
    }

    @Override
    public void displayMessage(String message) {
        out.println("INFO " + message);
    }

    public void displayPrompt(String message) {
        out.print(message);
    }

    @Override
    public void displayWarning(String message) {
        out.println();
        out.print("WARNING : " + message + ". PRESS ENTER TO CONTINUE.");
        try {
            in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void displayResults() {
        eraseScreen();
        displayMessage("GAME RESULTS");
        displayMessage("------------");
        for (Map.Entry<String, Integer> entry : gameStateReference.scoreBoard.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).toList().reversed()) {
            displayResult(entry.getKey(), entry.getValue());
        }
    }

    protected void displayResult(String playerName, Integer score) {
        out.println(playerName + " : " + score);
    }

    @Override
    public String promptForPlayerName(int playerNumber) {
        eraseScreen();
        displayPrompt("Insert name for player" + playerNumber + " : ");
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String promptForBoardHeight() {
        eraseScreen();
        displayPrompt("Insert height of the board : ");
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String promptForBoardWidth() {
        eraseScreen();
        displayPrompt("Insert width of the board : ");
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String promptForNumberOfPlayers() {
        eraseScreen();
        displayPrompt("Insert number of players : ");
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String promptForAction() {
        displayPrompt("Insert \"quit\" to quit the game\n");
        displayPrompt("Insert \"save\" to save the game\n");
        displayPrompt("Or make your move [ x1 y1 x2 y2 ] : ");
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String promptForGamemode() {
        eraseScreen();
        out.println("Select the game mode");
        out.println("\t1 - Player vs. Player");
        out.println("\t2 - Player vs. CPU");
        out.print(" : ");
        String choice;
        try {
            choice = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return switch (choice) {
            case "1" -> "PVP";
            case "2" -> "PVE";
            default -> null;
        };
    }

    public void eraseScreen() {
        for (int i = 0; i < 3; i++) {
            out.println();
        }
    }
}
