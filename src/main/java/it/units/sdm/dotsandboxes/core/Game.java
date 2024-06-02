package it.units.sdm.dotsandboxes.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import it.units.sdm.dotsandboxes.persistence.Savable;
import it.units.sdm.dotsandboxes.persistence.adapters.ScoreboardAdapter;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

public class Game implements Savable<Game> {

    private static final Gson serializer = new GsonBuilder().create();
    private final List<Player> players;

    @JsonAdapter(ScoreboardAdapter.class)
    private HashMap<Player, Integer> scoreBoard;

    private final Board gameBoard;
    private final ArrayList<Move> moves;
    private final List<Point> completedBoxes;

    public Game(int boardLength, int boardHeight, List<Player> players) {
        this.players = Objects.requireNonNull(players);
        if (this.players.size() < 2) {
            throw new IllegalStateException("Game requires a minimum of 2 players.");
        }
        this.players.forEach(Objects::requireNonNull);
        scoreBoard = new HashMap<>(this.players.size());
        for (Player p : players) {
            scoreBoard.put(p, 0);
        }
        gameBoard = new Board(boardLength, boardHeight);
        moves = new ArrayList<>();
        completedBoxes = new ArrayList<>();
    }

    public Game(int boardLength, int boardHeight, Player... players) {
        this(boardLength, boardHeight, Arrays.asList(players));
    }

    public Game() {
        this(0, 0, new Player("dummy1", Color.RED), new Player("dummy2", Color.BLUE));
    }

    public int getLastPlayerIndex() {
        if (moves.isEmpty()) {
            return -1;
        }
        return players.indexOf(moves.getLast().player());
    }

    public int getCurrentPlayerIndex() {
        return (getLastPlayerIndex() + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        // we chose to make the player1 start first every time
        if (moves.isEmpty())
            return this.players.getFirst();
        return this.players.get(getCurrentPlayerIndex());
    }

    public Player getLastPlayer() {
        // we chose to make the player1 start first every time
        if (moves.isEmpty())
            return null;
        return this.players.get(getLastPlayerIndex());
    }

    public int getPlayerScore(Player p) {
        return scoreBoard.get(p);
    }

    public void makeNextMove(Line line) {
        Line lineCandidate = new Line(getCurrentPlayer().color(), line);
        Move moveCandidate = new Move(getCurrentPlayer(), lineCandidate);
        gameBoard.addLine(lineCandidate);
        moves.add(moveCandidate);
    }

    public Board getBoard() {
        return gameBoard;
    }

    public boolean hasEnded() {
        return gameBoard.isBoardFull();
    }

    public void updateScore() {
        for (int i = 0; i < gameBoard.length(); i++) {
            for (int j = 0; j < gameBoard.height(); j++) {
                Point currentPoint = new Point(i, j);
                if (gameBoard.isBoxCompleted(currentPoint) && !completedBoxes.contains(currentPoint)) {
                    increasePlayerScoreByOne(getLastPlayer());
                    completedBoxes.add(new Point(i, j));
                }
            }
        }
    }

    private void increasePlayerScoreByOne(final Player p) {
        final int currentScore = scoreBoard.get(p);
        final int newScore = currentScore + 1;
        scoreBoard.replace(p, newScore);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Integer> getScores() {
        List<Integer> scores = new ArrayList<>(scoreBoard.size());
        for (Player player : players) {
            scores.add(scoreBoard.get(player));
        }
        return scores;
    }

    public List<Player> winners() {
        if (!hasEnded()) {
            return null; // or maybe throw an exception?
        } else {
            List<Player> sortedByScore = getPlayers().stream().sorted((p1, p2) -> getPlayerScore(p1) - getPlayerScore(p2)).toList();
            int maxScore = getPlayerScore(sortedByScore.getLast());
            return sortedByScore.stream().filter(player -> getPlayerScore(player) == maxScore).toList();
        }
    }

    @Override
    public byte[] save() {
        return serializer.toJson(this).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Game restore(byte[] data) {
        final Game restored = serializer.fromJson(new String(data), Game.class);
        final HashMap<Player, Integer> correctScoreBoard = new HashMap<>();
        for (Map.Entry<Player, Integer> entry : restored.scoreBoard.entrySet()) {
            correctScoreBoard.put(restored.players.stream()
                    .filter(p -> p.id().equals(entry.getKey().id()))
                    .findFirst()
                    .orElseThrow(), entry.getValue());
        }
        restored.scoreBoard = correctScoreBoard;
        return restored;
    }

    @Override
    public String toString() {
        return "Game{" +
                "players=" + players +
                ", scoreBoard=" + scoreBoard +
                ", gameBoard=" + gameBoard +
                ", moves=" + moves +
                ", completedBoxes=" + completedBoxes +
                '}';
    }
}
