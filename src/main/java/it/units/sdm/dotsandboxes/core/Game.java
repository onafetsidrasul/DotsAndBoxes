package it.units.sdm.dotsandboxes.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Game {

    final List<Player> players;
    Board gameBoard;
    ArrayList<Move> moves;

    public Game(int[] boardDimensions, List<Player> players) {
        this.players = Objects.requireNonNull(players);
        if (this.players.size() < 2) {
            throw new RuntimeException("Game requires a minimum of 2 players.");
        }
        this.players.forEach(Objects::requireNonNull);
        this.gameBoard = new Board(boardDimensions[0], boardDimensions[1]);
        this.moves = new ArrayList<>();
    }

    public Game(Player player1, Player player2) {
        this(new int[] { 5, 5 }, Arrays.asList(player1, player2));
    }

    public Player getCurrentPlayer() {
        // we chose to make the player1 start first every time
        if(moves.isEmpty())
            return this.players.getFirst();
        int lastPlayerIndex = players.indexOf(moves.getLast().player());
        int nextPlayerIndex = (lastPlayerIndex + 1) % players.size();
        return this.players.get(nextPlayerIndex);
    }

    public void makeNextMove(Line line) {
        Line lineCandidate = new Line(getCurrentPlayer().getColor(), line.x1(), line.y1(), line.x2(), line.y2());
        Move moveCandidate = new Move(getCurrentPlayer(), new Line(null,lineCandidate).hashCode());
        gameBoard.addLine(lineCandidate);
        moves.add(moveCandidate);
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public boolean hasEnded() {
        return gameBoard.isBoardFull();
    }

}
