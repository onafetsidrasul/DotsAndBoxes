package it.units.sdm.dotsandboxes.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;
import it.units.sdm.dotsandboxes.persistence.Savable;
import it.units.sdm.dotsandboxes.persistence.adapters.ScoreboardAdapter;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Game implements Savable<Game> {

    private static final Gson serializer = new GsonBuilder().create();

    public final List<String> players = new ArrayList<>();
    public final Map<String, Color> playerColorLUT;

    @JsonAdapter(ScoreboardAdapter.class)
    public Map<String, Integer> scoreBoard;

    public final Board board;
    public final Set<Point> completedBoxes;

    public Game(int boardLength, int boardHeight, SequencedCollection<String> players) throws InvalidInputException {
        if (Set.copyOf(players).size() != players.size()) { // allows with a single line to throw a NullPointerException if either players is null or has a null element
            throw new InvalidInputException("Two players have the same name.");
        }
        this.players.addAll(players);
        if (this.players.size() < 2) {
            throw new InvalidInputException("Game requires a minimum of 2 players.");
        }
        scoreBoard = new ConcurrentHashMap<>(this.players.size());
        playerColorLUT = new HashMap<>(this.players.size());
        int c = 0;
        for (String p : this.players) {
            scoreBoard.put(p, 0);
            playerColorLUT.put(p, Color.values()[c++]);
        }
        try {
            board = new Board(boardLength, boardHeight);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }
        completedBoxes = new HashSet<>();
    }

    public Game(int boardLength, int boardHeight, String... players) throws InvalidInputException {
        this(boardLength, boardHeight, Arrays.asList(players));
    }

    public Game() throws InvalidInputException {
        this(0, 0, "dummy1", "dummy2");
    }

    public int getLastPlayerIndex() {
        synchronized (board.lines()){
        if (board.lines().isEmpty()) {
            return -1;
        }
        return (board.lines().size() - 1) % players.size();}
    }

    public int getCurrentPlayerIndex() {
        return (getLastPlayerIndex() + 1) % players.size();
    }

    public String currentPlayer() {
        // we chose to make the player1 start first every time
        synchronized(board.lines()){
            if (board.lines().isEmpty())
                return this.players.getFirst();
            return this.players.get(getCurrentPlayerIndex());
        }

    }

    public String getLastPlayer() {
        // we chose to make the player1 start first every time
        synchronized (board.lines()){
            if (board.lines().isEmpty())
                return null;
            return this.players.get(getLastPlayerIndex());
        }

    }

    public int getPlayerScore(String p) {
        return scoreBoard.get(p);
    }

    public Color getPlayerColor(String p) {
        return playerColorLUT.get(p);
    }

    public void makeNextMove(Line line) throws InvalidInputException {
        ColoredLine lineCandidate = new ColoredLine(line, getPlayerColor(currentPlayer()));
        board.placeLine(lineCandidate);
    }

    public Board board() {
        return board;
    }

    public boolean hasEnded() {
        return board.isBoardFull();
    }

    public synchronized void updateScore() {
        for (int i = 0; i < board.width(); i++) {
            for (int j = 0; j < board.height(); j++) {
                Point currentPoint = new Point(i, j);
                if (board.isBoxCompleted(currentPoint) && !completedBoxes.contains(currentPoint)) {
                    increasePlayerScoreByOne(getLastPlayer());
                    completedBoxes.add(new Point(i, j));
                }
            }
        }
    }

    private synchronized void increasePlayerScoreByOne(String p) {
        final int currentScore = scoreBoard.get(p);
        final int newScore = currentScore + 1;
        scoreBoard.replace(p, newScore);
    }

    public SequencedCollection<String> players() {
        return players;
    }

    public Map<String, Integer> scoreBoard() {
        return scoreBoard;
    }

    public Map<String, Color> playerColorLUT(){
        return playerColorLUT;
    }

    public SequencedCollection<String> winners() {
        if (!hasEnded()) {
            return null; // or maybe throw an exception?
        } else {
            SequencedCollection<String> sortedByScore = players().parallelStream().sorted(Comparator.comparingInt(this::getPlayerScore)).toList();
            int maxScore = getPlayerScore(sortedByScore.getLast());
            return sortedByScore.stream().filter(player -> getPlayerScore(player) == maxScore).toList();
        }
    }

    @Override
    public byte[] serialized() {
        return serializer.toJson(this).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Game restore(byte[] data) {
        final Game restored = serializer.fromJson(new String(data), Game.class);
        final HashMap<String, Integer> correctScoreBoard = new HashMap<>();
        for (Map.Entry<String, Integer> entry : restored.scoreBoard.entrySet()) {
            correctScoreBoard.put(restored.players.stream()
                    .filter(p -> p.equals(entry.getKey()))
                    .findFirst()
                    .orElseThrow(), entry.getValue());
        }
        restored.scoreBoard = correctScoreBoard;
        return restored;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Score Board:").append("\n");
        scoreBoard.forEach((p,s)->sb.append(p).append(": ").append(s).append("\n"));
        sb.append("\n");
        sb.append("Game Board:").append("\n").append(board);
        sb.append("\n");
        return sb.toString();
    }
}
