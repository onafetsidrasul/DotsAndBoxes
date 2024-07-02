package it.units.sdm.dotsandboxes.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;
import it.units.sdm.dotsandboxes.persistence.Savable;
import it.units.sdm.dotsandboxes.persistence.adapters.ScoreboardAdapter;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.ToIntFunction;

public class Game implements Savable<Game> {

    private static final Gson serializer = new GsonBuilder().create();
    private final List<Player> players;

    @JsonAdapter(ScoreboardAdapter.class)
    private HashMap<String, Integer> scoreBoard;

    private final Board board;
    private final List<Point> completedBoxes;

    public Game(int boardLength, int boardHeight, List<Player> players) throws InvalidInputException {
        this.players = Objects.requireNonNull(players);
        if (this.players.size() < 2) {
            throw new InvalidInputException("Game requires a minimum of 2 players.");
        }
        this.players.forEach(Objects::requireNonNull);
        Set<Player> s = new HashSet<>();
        for (Player p : players) {
            if (s.contains(p)) {
                throw new InvalidInputException("Two players have the same name.");
            } else {
                s.add(p);
            }
        }
        scoreBoard = new HashMap<>(this.players.size());
        for (Player p : players) {
            scoreBoard.put(p.name(), 0);
        }
        board = new Board(boardLength, boardHeight);
        completedBoxes = new ArrayList<>();
    }

    public Game(int boardLength, int boardHeight, Player... players) throws InvalidInputException {
        this(boardLength, boardHeight, Arrays.asList(players));
    }

    public Game() throws InvalidInputException {
        this(0, 0, new Player("dummy1", Color.RED), new Player("dummy2", Color.BLUE));
    }

    public int getLastPlayerIndex() {
        if (board.lines().isEmpty()) {
            return -1;
        }
        return (board.lines().size() - 1) % players.size();
    }

    public int getCurrentPlayerIndex() {
        return (getLastPlayerIndex() + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        // we chose to make the player1 start first every time
        if (board.lines().isEmpty())
            return this.players.getFirst();
        return this.players.get(getCurrentPlayerIndex());
    }

    public Player getLastPlayer() {
        // we chose to make the player1 start first every time
        if (board.lines().isEmpty())
            return null;
        return this.players.get(getLastPlayerIndex());
    }

    public int getPlayerScore(Player p) {
        return scoreBoard.get(p.name());
    }

    public void makeNextMove(Line line) throws InvalidInputException {
        ColoredLine lineCandidate = new ColoredLine(line, getCurrentPlayer().color());
        board.placeLine(lineCandidate);
    }

    public Board getBoard() {
        return board;
    }

    public boolean hasEnded() {
        return board.isBoardFull();
    }

    public void updateScore() {
        for (int i = 0; i < board.length(); i++) {
            for (int j = 0; j < board.height(); j++) {
                Point currentPoint = new Point(i, j);
                if (board.isBoxCompleted(currentPoint) && !completedBoxes.contains(currentPoint)) {
                    increasePlayerScoreByOne(getLastPlayer());
                    completedBoxes.add(new Point(i, j));
                }
            }
        }
    }

    private void increasePlayerScoreByOne(final Player p) {
        final int currentScore = scoreBoard.get(p.name());
        final int newScore = currentScore + 1;
        scoreBoard.replace(p.name(), newScore);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Integer> getScores() {
        List<Integer> scores = new ArrayList<>(scoreBoard.size());
        for (Player player : players) {
            scores.add(scoreBoard.get(player.name()));
        }
        return scores;
    }

    public List<Player> winners() {
        if (!hasEnded()) {
            return null; // or maybe throw an exception?
        } else {
            //List<Player> sortedByScore = getPlayers().stream().sorted((p1, p2) -> getPlayerScore(p1) - getPlayerScore(p2)).toList();
            List<Player> sortedByScore = getPlayers().parallelStream().sorted(Comparator.comparingInt(this::getPlayerScore)).toList();
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
        final HashMap<String, Integer> correctScoreBoard = new HashMap<>();
        for (Map.Entry<String, Integer> entry : restored.scoreBoard.entrySet()) {
            correctScoreBoard.put(restored.players.stream()
                    .filter(p -> p.name().equals(entry.getKey()))
                    .findFirst()
                    .orElseThrow().toString(), entry.getValue());
        }
        restored.scoreBoard = correctScoreBoard;
        return restored;
    }

    @Override
    public String toString() {
        return "Game{" +
                "players=" + players +
                ", scoreBoard=" + scoreBoard +
                ", gameBoard=" + board +
                ", completedBoxes=" + completedBoxes +
                '}';
    }
}
