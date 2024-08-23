package it.units.sdm.dotsandboxes.controllers;

import it.units.sdm.dotsandboxes.core.*;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedQuit;
import it.units.sdm.dotsandboxes.views.IGameView;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Controller class that contains all the game's logic, such as the order of turns and end game conditions.
 */
public class IGameController {

    private final IGameView view;
    private Game game;
    private GameMode gamemode;
    private ComputerMoveStrategy computerMoveStrategy;
    private boolean isInitialized;
    private boolean setUpIsDone;
    private boolean gameIsOver;
    private Semaphore refreshUISem;
    private Semaphore gameOverCheckSem;
    private Semaphore inputHasBeenReceivedSem;
    private String input;   // inputs are in the form of strings, and follow the same syntax for all input methods (console, gui, etc.)

    public IGameController(IGameView view) {
        this.view = view;
    }

    /**
     * Let the controller and the UI perform an initialization step. After this method has returned successfully, the UI is
     * assumed to be ready to render the set-up process.
     *
     * @return true if the initialization has terminated successfully, false otherwise.
     */
    public boolean initialize() {
        isInitialized = false;
        setUpIsDone = false;
        gameIsOver = false;
        input = null;
        refreshUISem = new Semaphore(0);
        gameOverCheckSem = new Semaphore(1);
        inputHasBeenReceivedSem = new Semaphore(0);
        isInitialized = view.init(this);
        return isInitialized;
    }

    /**
     * Let the controller and the UI perform the game set-up. This method must be invoked after the initialization step.
     * After the method has been called, the UI is assumed to be ready to render the game.
     *
     * @return true if the set-up has terminated successfully, false otherwise.
     */
    public final boolean setUpGame() {
        boolean setUpIsDone = isInitialized;
        try {
            switch (gamemode = getGamemode()) {
                case PVE:
                    setUpGameVsComputer();
                    break;
                case PVP:
                    setUpGameVsPlayer();
                    break;
            }
        } catch (Exception e) {
            setUpIsDone = false;
        } finally {
            this.setUpIsDone = setUpIsDone;
        }
        return setUpIsDone;
    }

    private void setUpGameVsComputer() {
        int playerCount = 2;
        SequencedCollection<String> players = new ArrayList<>(playerCount);
        players.add(getPlayerName(1));
        players.add("CPU");
        computerMoveStrategy = ComputerMoveStrategy.RANDOM;
        finishGameSetup(players);
    }

    private void setUpGameVsPlayer() {
        int playerCount;
        do {
            playerCount = getPlayerCount();
            if (playerCount < 2) {
                sendWarning("You need at least 2 players!");
            }
            if (playerCount > Color.values().length) {
                sendWarning("Too many players! Max amount is " + Color.values().length);
            }
        } while (playerCount < 2 || playerCount > Color.values().length);
        SequencedCollection<String> players = new ArrayList<>(playerCount);
        for (int playerIndex = 1; playerIndex <= playerCount; playerIndex++) {
            players.add(getPlayerName(playerIndex));
        }
        finishGameSetup(players);
    }

    private void finishGameSetup(SequencedCollection<String> players) {
        int[] boardDimensions;
        boolean dimensionsAreValid;
        do {
            dimensionsAreValid = true;
            boardDimensions = getBoardDimensions();
            try {
                game = new Game(boardDimensions[0], boardDimensions[1], players);
            } catch (InvalidInputException | IllegalArgumentException e) {
                sendWarning(e.getMessage());
                dimensionsAreValid = false;
            }
        } while (!dimensionsAreValid);
        setUpIsDone = view.configure(game);
    }

    /**
     * Begins the actual game with the proper set-up.
     * @throws UserHasRequestedQuit if the player has requested to end the game.
     */
    public final void startGame() throws UserHasRequestedQuit, InvalidInputException {
        try {
            switch (gamemode) {
                case PVE:
                    startGameVsComputer();
                    break;
                case PVP:
                    startGameVsPlayer();
                    break;
            }
        } catch (UserHasRequestedQuit e) {
            view.displayMessage("Bye!");
            throw e;
        }
        displayResults();
    }

    private void startGameVsComputer() throws UserHasRequestedQuit, InvalidInputException {
        if (game == null || !setUpIsDone) {
            throw new IllegalStateException("Game has not been set up!");
        }
        view.startGameUI();
        do {
            if (game.getCurrentPlayerIndex() + 1 == 1) {
                refreshUISem.release();
                view.signalWhenUIRefreshed();
                try {
                    Line line = getAction();
                    makeMove(line);
                } catch (InvalidInputException e) {
                    sendWarning(e.getMessage());
                }
            } else {
                makeGeneratedMove();
            }
            if (game.hasEnded()) {
                break;
            }
            gameOverCheckSem.release();
        } while (true);
        endGame();
    }

    private void startGameVsPlayer() throws UserHasRequestedQuit, InvalidInputException {
        if (game == null || !setUpIsDone) {
            throw new IllegalStateException("Game has not been set up!");
        }
        view.startGameUI();
        do {
            refreshUISem.release();
            try {
                Line line = getAction();
                makeMove(line);
            } catch (InvalidInputException e) {
                sendWarning(e.getMessage());
            }
            if (game.hasEnded()) {
                break;
            }
            gameOverCheckSem.release();
        } while (true);
        endGame();
    }

    private void makeGeneratedMove(){
        /* In theory, computer-generated moves should always be valid. For this reason, we can suppose this method never throws.
         * If it does, the game must crash, as it means the computer-generation algorithm is wrong and should be fixed.
         */
        try {
            makeMove(ComputerAgent.generateMove(computerMoveStrategy, game.board()));
        } catch (InvalidInputException e) {
            throw new RuntimeException("The CPU generated an invalid move. " + e);
        }
    }

    /**
     * Get the number of players that will be generated, meaning how many will play the current game.
     *
     * @return the player count.
     */
    protected int getPlayerCount() {
        return Integer.parseInt(view.promptForNumberOfPlayers());
    }

    /**
     * Get the name to be assigned to the player with the passed number.
     *
     * @param playerNumber ordinal of the player being created.
     * @return the string literal for the name to be assigned to the player being created.
     */
    protected String getPlayerName(int playerNumber) {
        return view.promptForPlayerName(playerNumber);
    }

    /**
     * Get the number of columns and rows of the game board.
     * @return an integer array containing two elements: int[2] { height, width }.
     */
    protected int[] getBoardDimensions() {
        try {
            return Arrays.stream(view.promptForBoardDimensions()).mapToInt(Integer::parseInt).toArray();
        } catch (NumberFormatException e) {
            sendWarning("Invalid input. Please enter valid integer coordinates");
            return null;
        }
    }

    /**
     * Wait to receive an event from the player.
     *
     * @return the Line being played in the current turn by the playing Player (determined by the game instance).
     * @see Game#makeNextMove(Line)
     * @throws UserHasRequestedQuit if the user wants to quit the game.
     * @throws InvalidInputException if something unexpected happened in the entire chain of input.
     */
    protected Line getAction() throws InvalidInputException, UserHasRequestedQuit {
        Line candidate;
        try {
            inputHasBeenReceivedSem.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        candidate = parseLineString(input);

        return candidate;
    }

    private Line parseLineString(String input) throws InvalidInputException, UserHasRequestedQuit {
        if ("quit".equals(input)) {
            endGame();
            throw new UserHasRequestedQuit();
        }
        final List<String> coords = List.of(input.split(" "));
        if (coords.size() != 4) {
            throw new InvalidInputException("Invalid input. Please enter four space-separated coordinates");
        }
        int[] parsedCoords = coords.stream().mapToInt(Integer::parseInt).toArray();
        return parsedCoords == null ? null : new Line(parsedCoords[0], parsedCoords[1], parsedCoords[2], parsedCoords[3]);
    }

    final void makeMove(Line line) throws InvalidInputException {
        game.makeNextMove(line);
        game.updateScore();
    }

    /**
     * Terminate the game.
     */
    public void endGame() {
        gameIsOver = true;
        gameOverCheckSem.release();
        refreshUISem.release();
    }

    /**
     * Displays the scoreboard in decreasing score order.
     */
    public void displayResults() {
        view.displayResults();
    }

    /**
     * @return the intent of the player after the game has ended.
     */
    public PostGameIntent getPostGameIntent() throws IOException {
        return view.promptForPostGameIntent().equals("NEW") ? PostGameIntent.NEW_GAME : PostGameIntent.END_GAME;
    }

    /**
     * @return the chosen game mode.
     */
    public GameMode getGamemode() {
        return switch (view.promptForGamemode()) {
            case "PVP" -> GameMode.PVP;
            case "PVE" -> GameMode.PVE;
            default -> throw new IllegalArgumentException("Unexpected value");
        };
    }

    /**
     * Send a warning to the player through the UI.
     * @param message warning to send.
     */
    public void sendWarning(String message) {
        view.displayWarning(message);
    }

    public boolean sendEndGameWarning(){
        return view.promptForPostGameIntent().equals("END");
    }

    /**
     * @return if the game is over.
     */
    public boolean gameIsOver() {
        return gameIsOver;
    }

    /**
     * @param input the input received from the user in string form.
     */
    public void writeInput(String input) {
        this.input = input;
    }

    /**
     * Unlocks the controller after an input has been passed.
     */
    public void resumeAfterInputReception(){
        inputHasBeenReceivedSem.release();
    }

    /**
     * Stops the controller to let others mutually check the game over state.
     */
    public void stopToCheckIfGameOver(){
        try {
            gameOverCheckSem.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stops the controller while the UI refreshes.
     */
    public void stopToRefreshUI(){
        try {
            refreshUISem.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
    }


}

class ComputerAgent {

    private ComputerAgent(){}

    public static Line generateMove(ComputerMoveStrategy strategy, Board board) {
        return switch (strategy) {
            case RANDOM -> generateRandomMove(board);
        };
    }

    private static Line generateRandomMove(Board board) {
        Line candidate;
        boolean lineAlreadyExists;
        do {
            candidate = generateRandomCandidate(board);
            final Line finalCandidate = candidate;
            lineAlreadyExists = board.lines().parallelStream().anyMatch(l -> l.hasSameEndpointsAs(finalCandidate));
        } while (lineAlreadyExists);
        return candidate;
    }

    private static Line generateRandomCandidate(Board board) {
        Line candidate;
        Point p1 = new Point((int) Math.floor(Math.random() * (board.width())), (int) Math.floor(Math.random() * (board.height())));
        Point p2;
        do {
            if (Math.random() >= 0.5) {
                p2 = new Point(p1.x(), p1.y() + (Math.random() >= 0.5 ? 1 : -1));
            } else {
                p2 = new Point(p1.x() + (Math.random() >= 0.5 ? 1 : -1), p1.y());
            }
        } while (!isValidPoint(p2.x(), p2.y(), board));
        try {
            candidate = new Line(p1.x(), p1.y(), p2.x(), p2.y());
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
        return candidate;
    }

    private static boolean isValidPoint(int x, int y, Board board) {
        return x >= 0 && x < board.width() && y >= 0 && y < board.height();
    }
}

