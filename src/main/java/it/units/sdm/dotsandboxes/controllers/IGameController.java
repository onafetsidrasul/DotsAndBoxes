package it.units.sdm.dotsandboxes.controllers;

import it.units.sdm.dotsandboxes.core.*;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedQuit;
import it.units.sdm.dotsandboxes.exceptions.UserHasRequestedSave;
import it.units.sdm.dotsandboxes.persistence.Savable;
import it.units.sdm.dotsandboxes.views.IGameView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SequencedCollection;
import java.util.concurrent.Semaphore;

public abstract class IGameController implements Savable<IGameController> {

    protected IGameView view;
    protected Game game;
    protected Gamemode gamemode;
    protected boolean isInitialized = false;
    protected boolean setUpIsDone = false;
    public Semaphore readyToRefreshUISem = new Semaphore(0);
    public boolean gameIsOver = false;
    public Semaphore inputHasBeenReceivedSem = new Semaphore(0);
    public String input = null;

    public record SavedIGameController(
            String gameControllerClassName,
            String gameViewClassName,
            String gameData,
            String gameModeName
    ) {
    }

    public IGameController(final IGameView view) {
        this.view = view;
    }

    public IGameController() {
    }


    /**
     * Let the controller and the UI perform an initialization step. After this method has returned successfully, the UI is
     * assumed to be ready to render the set-up process.
     *
     * @return true if the initialization has terminated successfully, false otherwise.
     */
    public abstract boolean initialize();

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
    }

    public final void startGameVsComputer() throws IOException, UserHasRequestedQuit, UserHasRequestedSave {
        if (game == null || !setUpIsDone) {
            throw new IllegalStateException("Game has not been set up!");
        }
        view.startGameUI();
        while (!game.hasEnded()) {
            Line line = null;
            if (game.getCurrentPlayerIndex() + 1 == 1) {
                boolean inputIsValid;
                do {
                    readyToRefreshUISem.release();
                    inputIsValid = true;
                    try {
                        line = getAction();
                    } catch (InvalidInputException e) {
                        sendWarning(e.getMessage());
                        inputIsValid = false;
                    }
                } while (!inputIsValid);
            } else {
                view.isRefreshingUISem.release();
                line = generateMove(ComputerMoveStrategy.RANDOM);
            }
            tryMove(line);
        }
        endGame();
    }

    public final void startGameVsPlayer() throws IOException, UserHasRequestedQuit, UserHasRequestedSave {
        if (game == null || !setUpIsDone) {
            throw new IllegalStateException("Game has not been set up!");
        }
        view.startGameUI();
        while (!game.hasEnded()) {
            readyToRefreshUISem.release();
            Line line = null;
            boolean inputIsValid;
            do {
                inputIsValid = true;
                try {
                    line = getAction();
                } catch (InvalidInputException e) {
                    sendWarning(e.getMessage());
                    inputIsValid = false;
                    readyToRefreshUISem.release();
                }
            } while (!inputIsValid);
            tryMove(line);
        }
        endGame();
    }

    private void tryMove(Line line) {
        try {
            view.isRefreshingUISem.acquire();
            makeMove(line);
        } catch (InvalidInputException e) {
            sendWarning(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Line generateMove(ComputerMoveStrategy strategy) {
        return switch (strategy) {
            case RANDOM -> generateRandomMove();
        };
    }

    private Line generateRandomMove() {
        Line candidate;
        boolean lineAlreadyExists;
        do {
            candidate = randomCandidate(new int[]{game.board().height(), game.board().width()});
            final Line finalCandidate = candidate;
            lineAlreadyExists = game.board().lines().parallelStream().anyMatch(l -> l.hasSameEndpointsAs(finalCandidate));
        } while (lineAlreadyExists);
        return candidate;
    }

    private static Line randomCandidate(int[] dims) {
        Line candidate;
        Point p1 = getFirstPoint(dims);
        Point p2;
        do {
            p2 = getSecondPoint(p1);
        } while (!isValidPoint(p2.x(), p2.y(), dims));
        try {
            candidate = new Line(p1.x(), p1.y(), p2.x(), p2.y());
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
        return candidate;
    }

    private static Point getFirstPoint(int[] dims) {
        int x1 = (int) Math.floor(Math.random() * dims[0]);
        int y1 = (int) Math.floor(Math.random() * dims[1]);
        return new Point(x1, y1);
    }

    private static Point getSecondPoint(Point p) {
        if (Math.random() >= 0.5) {
            return new Point(p.x(), p.y() + (Math.random() >= 0.5 ? 1 : -1));
        } else {
            return new Point(p.x() + (Math.random() >= 0.5 ? 1 : -1), p.y());
        }
    }

    private static boolean isValidPoint(int x, int y, int[] dims) {
        return x >= 0 && x < dims[0] && y >= 0 && y < dims[1];
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
        Line candidate = null;
        do {
            try {
                inputHasBeenReceivedSem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (input != null && !input.isEmpty()) {
                candidate = parseLineString(input);
            }
        } while (candidate == null);
        return candidate;
    }

    private Line parseLineString(String input) throws InvalidInputException, UserHasRequestedQuit, UserHasRequestedSave {
        if ("quit".equals(input)) {
            gameIsOver = true;
            throw new UserHasRequestedQuit();
        }
        if ("save".equals(input)) {
            gameIsOver = true;
            throw new UserHasRequestedSave();
        }
        final List<String> coords = List.of(input.split(" "));
        if (coords.size() != 4) {
            sendWarning("Invalid input. Please enter four space-separated coordinates");
            return null;
        }
        int[] parsedCoords = coords.stream().mapToInt(Integer::parseInt).toArray();
        return parsedCoords == null ? null : new Line(parsedCoords[0], parsedCoords[1], parsedCoords[2], parsedCoords[3]);
    }

    final void makeMove(Line line) throws InvalidInputException {
        game.makeNextMove(line);
        game.updateScore();
    }

    /**
     * Notify the UI to terminate the current game specifying the passed player as the winner.
     */
    public void endGame(){
        view.displayResults();
        gameIsOver = true;
    }

    public PostGameIntent getPostGameIntent() throws IOException {
        return view.promptForPostGameIntent().equals("NEW") ? PostGameIntent.NEW_GAME : PostGameIntent.END_GAME;
    }

    public Gamemode getGamemode() throws IOException{
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
}

