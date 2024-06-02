package it.units.sdm.dotsandboxes.controllers;

import it.units.sdm.dotsandboxes.GameSession;
import it.units.sdm.dotsandboxes.core.*;
import it.units.sdm.dotsandboxes.persistence.Savable;
import it.units.sdm.dotsandboxes.views.IGameView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class IGameController implements Savable<IGameController> {

    protected IGameView view;
    protected Game game;
    protected Gamemode gamemode;
    protected boolean isSetupDone = false;

    public record SavedIGameController(
            String gameControllerClassName,
            String gameViewClassName,
            String gameData,
            String gameModeName
    ) {}

    public IGameController(final IGameView view) {
        this.view = view;
    }


    /**
     * Let the controller and the UI perform an initialization step. After this method has returned successfully, the UI is
     * assumed to be ready to render the game and the game is ready to be started.
     *
     * @return whether the initialization process completed successfully
     */
    public abstract boolean initialize();

    /**
     * Acquires from the user all the information necessary to start a game
     */
    public final void setUpGame() throws IOException {
        switch (gamemode = getGamemode()) {
            case PVE:
                setUpGameVsComputer();
                break;
            case PVP:
                setUpGameVsPlayer();
                break;
        }
        this.isSetupDone = true;
    }

    public final void setUpGameVsComputer() throws IOException {
        int playerCount = 2;
        List<Player> players = new ArrayList<>(playerCount);
        players.add(new Player(getPlayerName(1), Color.values()[1 % Color.values().length]));
        players.add(new Player("CPU", Color.values()[2 % Color.values().length]));
        int[] boardDimensions = getBoardDimensions();
        game = new Game(boardDimensions[0], boardDimensions[1], players);
    }

    public final void setUpGameVsPlayer() throws IOException {
        int playerCount = getPlayerCount();
        if (playerCount < 2) {
            throw new IllegalArgumentException("You need at least 2 players!");
        }
        if (playerCount > Color.values().length) {
            throw new IllegalArgumentException("Too many players! Max amount is " + Color.values().length);
        }
        List<Player> players = new ArrayList<>(playerCount);
        for (int playerIndex = 1; playerIndex <= playerCount; playerIndex++) {
            final Color playerColor = Color.values()[playerIndex % Color.values().length];
            players.add(new Player(getPlayerName(playerIndex), playerColor));
        }
        int[] boardDimensions = getBoardDimensions();
        game = new Game(boardDimensions[0], boardDimensions[1], players);
    }

    public final void startGame() throws IOException {
        switch (gamemode) {
            case PVE:
                startGameVsComputer();
            case PVP:
                startGameVsPlayer();
        }
    }

    public final void startGameVsComputer() throws IOException {
        if (game == null) {
            throw new IllegalStateException("Game has not been set up!");
        }
        view.updateUI(game.getBoard(), game.getPlayers(), game.getScores(), game.getCurrentPlayer());
        while (!game.hasEnded()) {
            final Line line;
            if (game.getCurrentPlayerIndex() + 1 == 1) {
                line = getLine(game.getCurrentPlayer());
            } else {
                line = generateMove(ComputerMoveStrategy.RANDOM);
            }
            makeMove(line);
            view.updateUI(game.getBoard(), game.getPlayers(), game.getScores(), game.getCurrentPlayer());
        }
        endGame(game.winners());
    }

    public final void startGameVsPlayer() throws IOException {
        if (game == null) {
            throw new IllegalStateException("Game has not been set up!");
        }
        view.updateUI(game.getBoard(), game.getPlayers(), game.getScores(), game.getCurrentPlayer());
        while (!game.hasEnded()) {
            final Line line = getLine(game.getCurrentPlayer());
            makeMove(line);
            view.updateUI(game.getBoard(), game.getPlayers(), game.getScores(), game.getCurrentPlayer());
        }
        endGame(game.winners());
    }

    private Line generateMove(ComputerMoveStrategy strategy) {
        return switch (strategy) {
            case RANDOM -> generateRandomMove();
        };
    }

    private Line generateRandomMove() {
        Line candidate;
        do {
            candidate = Line.normalize(randomCandidate(new int[]{game.getBoard().height(), game.getBoard().length()}));
        } while (game.getBoard().getLines().containsValue(candidate));
        return candidate;
    }

    public static Line randomCandidate(int[] dims) {
        Line candidate;
        Point p1 = getFirstPoint(dims);
        Point p2;
        do {
            p2 = getSecondPoint(p1);
        } while (!isValidPoint(p2.x(), p2.y(), dims));
        candidate = new Line(p1.x(), p1.y(), p2.x(), p2.y());
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
    abstract int getPlayerCount() throws IOException;

    /**
     * Get the name to be assigned to the player with the passed number and color.
     *
     * @param playerNumber ordinal of the player being created
     * @return the string literal for the name to be assigned to the player being created
     */
    abstract String getPlayerName(int playerNumber) throws IOException;

    /**
     * Get the number of rows and columns of the game board being created, either asking for user input or
     * returning a fixed value
     *
     * @return an integer array containing two elements: int[2] { width, height }
     */
    abstract int[] getBoardDimensions() throws IOException;

    /**
     * Wait for the UI to receive an event of a game turn being played by the user.
     *
     * @return the Line being played in the current turn by the playing Player (determined by the game instance)
     * @see Game#makeNextMove(Line)
     */
    abstract Line getLine(Player player) throws IOException;

    final void makeMove(Line line) {
        try {
            game.makeNextMove(new Line(line.p1().x(), line.p1().y(), line.p2().x(), line.p2().y()));
            game.updateScore();
        } catch (RuntimeException e) {
            view.displayIllegalMoveWarning(line);
        }
    }

    /**
     * Notify the UI to terminate the current game specifying the passed player as the winner.
     *
     * @param winner the winning player
     */
    abstract void endGame(List<Player> winner);

    public abstract PostGameIntent getPostGameIntent() throws IOException;

    public abstract Gamemode getGamemode() throws IOException;

    public boolean isSetupDone() {
        return isSetupDone;
    }

    @Override
    public byte[] save() {
        final String gameData = new String(
                encoder.encode(game.save()), StandardCharsets.UTF_8);
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
            restored.isSetupDone = true;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return restored;
    }
}

