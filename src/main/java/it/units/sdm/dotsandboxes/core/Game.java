package it.units.sdm.dotsandboxes.core;

import java.util.*;

public class Game {

    private final List<Player> players;

    private final HashMap<Player, Integer> scoreBoard;
    private final Board gameBoard;
    private final ArrayList<Move> moves;
    private final List<Point> completedBoxes;

    public Game(int boardLength, int boardHeight, List<Player> players) {
        this.players = Objects.requireNonNull(players);
        if (this.players.size() < 2) {
            throw new RuntimeException("Game requires a minimum of 2 players.");
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

    public int getLastPlayerIndex() {
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

    public int getPlayerScore(Player p){
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

    public List<Player> getPlayers(){
        return players;
    }

}
