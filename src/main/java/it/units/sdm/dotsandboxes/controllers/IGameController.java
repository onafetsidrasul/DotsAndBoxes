package it.units.sdm.dotsandboxes.controllers;

import it.units.sdm.dotsandboxes.core.*;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedQuit;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedSave;
import it.units.sdm.dotsandboxes.persistence.Savable;
import it.units.sdm.dotsandboxes.views.IGameView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Semaphore;

public class IGameController implements Savable<IGameController> {

    private IGameView view;
    private Game game;
    private Gamemode gamemode;
    private ComputerMoveStrategy computerMoveStrategy;
    private boolean isInitialized;
    private boolean setUpIsDone;
    private Semaphore refreshUISem;
    private boolean gameIsOver;
    private Semaphore gameOverCheckSem;
    private Semaphore inputHasBeenReceivedSem;
    private String input;

    public record SavedIGameController(
            String gameControllerClassName,
            String gameViewClassName,
            String gameData,
            String gameModeName
    ) {
    }

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
     * After the method has been called the UI is assumed to be ready to render the game.
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

    public final void setUpGameVsComputer() throws IOException {
        int playerCount = 2;
        SequencedCollection<String> players = new ArrayList<>(playerCount);
        players.add(getPlayerName(1));
        players.add("CPU");
        computerMoveStrategy = ComputerMoveStrategy.RANDOM;
        finishGameSetup(players);
    }

    private void finishGameSetup(SequencedCollection<String> players) throws IOException {
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

    public final void setUpGameVsPlayer() throws IOException {
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

    public final void startGame() throws IOException, UserHasRequestedQuit, UserHasRequestedSave {
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
        } catch (UserHasRequestedSave e) {
            view.displayMessage("Saving game...");
            throw e;
        }
        displayResults();
    }

    public final void startGameVsComputer() throws IOException, UserHasRequestedQuit, UserHasRequestedSave {
        if (game == null || !setUpIsDone) {
            throw new IllegalStateException("Game has not been set up!");
        }
        view.startGameUI();
        do {
            if (game.getCurrentPlayerIndex() + 1 == 1) {
                refreshUISem.release();
                try {
                    view.isRefreshingUISem.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
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

    public final void startGameVsPlayer() throws IOException, UserHasRequestedQuit, UserHasRequestedSave {
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

    private void makeGeneratedMove() {
        try {
            makeMove(generateMove());
        } catch (InvalidInputException e) {
            throw new RuntimeException("The CPU generated an invalid move. " + e);
        }
    }

    private Line generateMove() {
        return switch (computerMoveStrategy) {
            case RANDOM -> generateRandomMove();
        };
    }

    private Line generateRandomMove() {
        Line candidate;
        boolean lineAlreadyExists;
        do {
            candidate = generateRandomCandidate();
            final Line finalCandidate = candidate;
            lineAlreadyExists = game.board().lines().parallelStream().anyMatch(l -> l.hasSameEndpointsAs(finalCandidate));
        } while (lineAlreadyExists);
        return candidate;
    }

    private Line generateRandomCandidate() {
        Line candidate;
        Point p1 = new Point((int) Math.floor(Math.random() * (game.board().width())), (int) Math.floor(Math.random() * (game.board().height())));
        Point p2;
        do {
            if (Math.random() >= 0.5) {
                p2 = new Point(p1.x(), p1.y() + (Math.random() >= 0.5 ? 1 : -1));
            } else {
                p2 = new Point(p1.x() + (Math.random() >= 0.5 ? 1 : -1), p1.y());
            }
        } while (!isValidPoint(p2.x(), p2.y()));
        try {
            candidate = new Line(p1.x(), p1.y(), p2.x(), p2.y());
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
        return candidate;
    }

    private boolean isValidPoint(int x, int y) {
        return x >= 0 && x < game.board().width() && y >= 0 && y < game.board().height();
    }

    /**
     * Get the number of players that will be generated and therefore will play the current game.
     *
     * @return player count [1, ]
     */
    protected int getPlayerCount() throws IOException {
        return Integer.parseInt(view.promptForNumberOfPlayers());
    }

    /**
     * Get the name to be assigned to the player with the passed number and color.
     *
     * @param playerNumber ordinal of the player being created
     * @return the string literal for the name to be assigned to the player being created
     */
    protected String getPlayerName(int playerNumber) throws IOException {
        return view.promptForPlayerName(playerNumber);
    }

    /**
     * Get the number of rows and columns of the game board being created, either asking for user input or
     * returning a fixed value
     *
     * @return an integer array containing two elements: int[2] { width, height }
     */
    protected int[] getBoardDimensions() throws IOException {
        try {
            return Arrays.stream(view.promptForBoardDimensions()).mapToInt(Integer::parseInt).toArray();
        } catch (NumberFormatException e) {
            sendWarning("Invalid input. Please enter valid integer coordinates");
            return null;
        }
    }

    /**
     * Wait for the UI to receive an event of a game turn being played by the user.
     *
     * @return the Line being played in the current turn by the playing Player (determined by the game instance)
     * @see Game#makeNextMove(Line)
     */
    protected Line getAction() throws IOException, InvalidInputException, UserHasRequestedSave, UserHasRequestedQuit {
        Line candidate;
        try {
            inputHasBeenReceivedSem.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        candidate = parseLineString(input);

        return candidate;
    }

    private Line parseLineString(String input) throws InvalidInputException, UserHasRequestedQuit, UserHasRequestedSave {
        if ("quit".equals(input)) {
            endGame();
            throw new UserHasRequestedQuit();
        }
        if ("save".equals(input)) {
            endGame();
            throw new UserHasRequestedSave();
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
     * Notify the UI to terminate the current game.
     */
    public void endGame() {
        gameIsOver = true;
        gameOverCheckSem.release();
        refreshUISem.release();
    }

    public void displayResults() {
        view.displayResults();
    }

    public PostGameIntent getPostGameIntent() throws IOException {
        return view.promptForPostGameIntent().equals("NEW") ? PostGameIntent.NEW_GAME : PostGameIntent.END_GAME;
    }

    public Gamemode getGamemode() throws IOException {
        return switch (view.promptForGamemode()) {
            case "PVP" -> Gamemode.PVP;
            case "PVE" -> Gamemode.PVE;
            default -> throw new IllegalArgumentException("Unexpected value");
        };
    }

    public boolean isSetUpIsDone() {
        return setUpIsDone;
    }

    public void sendWarning(String message) {
        view.displayWarning(message);
    }

    @Override
    public byte[] serialized() {
        final String gameData = new String(
                encoder.encode(game.serialized()), StandardCharsets.UTF_8);
        final String payload = gson.toJson(new SavedIGameController(
                this.getClass().getName(),
                view.getClass().getName(),
                gameData,
                gamemode.name()
        ));
        return payload.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public IGameController restore(byte[] data) {
        final SavedIGameController savedSession =
                gson.fromJson(new String(data), SavedIGameController.class);
        final IGameController restored;
        try {
            restored = (IGameController) Class
                    .forName(savedSession.gameControllerClassName)
                    .getConstructor()
                    .newInstance();
            restored.view = (IGameView) Class
                    .forName(savedSession.gameViewClassName)
                    .getConstructor()
                    .newInstance();
            restored.game = new Game().restore(decoder.decode(savedSession.gameData));
            restored.gamemode = Gamemode.valueOf(savedSession.gameModeName);
            restored.setUpIsDone = true;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return restored;
    }

    public boolean gameIsOver() {
        return gameIsOver;
    }

    public void writeInput(String input) {
        this.input = input;
    }
    
    public void resumeAfterInputReception(){
        inputHasBeenReceivedSem.release();
    }

    public void stopToCheckIfGameOver(){
        try {
            gameOverCheckSem.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

