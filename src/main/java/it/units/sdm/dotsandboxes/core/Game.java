package it.units.sdm.dotsandboxes.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Game {

    final List<Player> players;
    Board gameBoard;
    ArrayList<Move> moves;
    final List<Point> completedBoxes = new ArrayList<>();
    public Game(List<Player> players, int boardX, int boardY) {
        this.players = Objects.requireNonNull(players);
        if (this.players.size() < 2) {
            throw new RuntimeException("Game requires a minimum of 2 players.");
        }
        this.players.forEach(Objects::requireNonNull);
        this.gameBoard = new Board(boardX, boardY);
        this.moves = new ArrayList<>();
    }

    public Game(Player player1, Player player2, int boardX, int boardY) {
        this(Arrays.asList(player1, player2), boardX, boardY);
    }

    public Game(Player player1, Player player2) {
        this(player1, player2, 5, 5);
    }

    public Player getNextPlayer() {
        // we chose to make the player1 start first every time
        if(moves.isEmpty())
            return this.players.getFirst();
        int lastPlayerIndex = players.indexOf(moves.getLast().player());
        int nextPlayerIndex = (lastPlayerIndex + 1) % players.size();
        return this.players.get(nextPlayerIndex);
    }

    public Player getCurrentPlayer() {
        return this.moves.getLast().player();
    }

    public void makeNextMove(Line line) {
        Line lineCandidate = new Line(getNextPlayer().getColor(), line.p1(), line.p2());
        Move moveCandidate = new Move(getNextPlayer(), new Line(null, lineCandidate).hashCode());
        gameBoard.addLine(lineCandidate);
        moves.add(moveCandidate);
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public boolean hasEnded() {
        return gameBoard.isBoardFull();
    }

    public void updateScore() {
        for (int i = 0; i < gameBoard.getX_dimension(); i++) {
            for (int j = 0; j < gameBoard.getY_dimension(); j++) {
                Point currentPoint= new Point(i,j);
                if (gameBoard.isBoxCompleted(currentPoint) && !completedBoxes.contains(currentPoint)) {
                    getCurrentPlayer().increaseScore();
                    completedBoxes.add(new Point(i,j));
                }
            }
        }
    }
}
