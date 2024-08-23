package it.units.sdm.dotsandboxes.core;

import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that models the state of a single game of Dots and Boxes.
 */
public class Game {

    private final List<String> players = new ArrayList<>();
    private final Map<String, Color> playerColorLUT;
    private final Map<String, Integer> scoreBoard;
    private final Board board;
    private final Set<Point> completedBoxes;

    public Game(int boardHeight, int boardWidth, SequencedCollection<String> players) throws InvalidInputException {
        if (Set.copyOf(players).size() != players.size()) {
            // allows, with a single line, to throw a NullPointerException if either players is null or has a null element
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
            board = new Board(boardHeight, boardWidth);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }
        completedBoxes = new HashSet<>();
    }

    public Game(int boardHeight, int boardWidth, String... players) throws InvalidInputException {
        this(boardHeight, boardWidth,  Arrays.asList(players));
    }

    /**
     * @return the index of the previous player.
     */
    public int getLastPlayerIndex() {
        synchronized (board.lines()) {
            if (board.lines().isEmpty()) {
                return -1;
            }
            return (board.lines().size() - 1) % players.size();
        }
    }

    /**
     * @return the index of the current player.
     */
    public int getCurrentPlayerIndex() {
        return (getLastPlayerIndex() + 1) % players.size();
    }

    /**
     * @return the name of the current player.
     */
    public String getCurrentPlayer() {
        // we chose to make the player1 start first every time
        synchronized (board.lines()) {
            if (board.lines().isEmpty())
                return this.players.getFirst();
            return this.players.get(getCurrentPlayerIndex());
        }

    }

    /**
     * @return the name of the previous player.
     */
    public String getLastPlayer() {
        // we chose to make the player1 start first every time
        synchronized (board.lines()) {
            if (board.lines().isEmpty())
                return null;
            return this.players.get(getLastPlayerIndex());
        }

    }

    /**
     * @param p the name of the player.
     * @return the player's score.
     */
    public int getPlayerScore(String p) {
        return scoreBoard.get(p);
    }

    /**
     * @param p the name of the player.
     * @return the player's color.
     */
    public Color getPlayerColor(String p) {
        return playerColorLUT.get(p);
    }

    public void makeNextMove(Line line) throws InvalidInputException {
        ColoredLine lineCandidate = line == null ? null : new ColoredLine(line, getPlayerColor(getCurrentPlayer()));
        board.placeLine(lineCandidate);
    }

    /**
     * @return the state of the board.
     */
    public final Board board() {
        return board;
    }

    /**
     * @return if the board is full.
     */
    public boolean hasEnded() {
        return board.isBoardFull();
    }

    /**
     * Checks the lines on the board to compute the scores.
     */
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

    /**
     * @return the list of players.
     */
    public List<String> players() {
        return players;
    }

    /**
     * @return the score board.
     */
    public Map<String, Integer> scoreBoard() {
        return scoreBoard;
    }

    /**
     * @return the LUT of the players' colors.
     */
    public Map<String, Color> playerColorLUT() {
        return playerColorLUT;
    }

    /**
     * @return the winners, in descending score order.
     */
    public SequencedCollection<String> winners() {
        if (!hasEnded()) {
            return null;
        } else {
            SequencedCollection<String> sortedByScore = players().parallelStream().sorted(Comparator.comparingInt(this::getPlayerScore)).toList();
            int maxScore = getPlayerScore(sortedByScore.getLast());
            return sortedByScore.stream().filter(player -> getPlayerScore(player) == maxScore).toList();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Score Board:").append("\n");
        scoreBoard.forEach((p, s) -> sb.append(p).append(": ").append(s).append("\n"));
        sb.append("\n");
        sb.append("Game Board:").append("\n").append(board);
        sb.append("\n");
        return sb.toString();
    }
}
