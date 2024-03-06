package it.units.sdm.dotsandboxes.core;

import java.util.*;
import java.util.stream.Collectors;

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

    public int getLastPlayerIndex(){
        return players.indexOf(moves.get(moves.size()-1).player());
    }
    public int getCurrentPlayerIndex(){
        return (getLastPlayerIndex() + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        // we chose to make the player1 start first every time
        if(moves.isEmpty())
            return this.players.get(0);
        return this.players.get(getCurrentPlayerIndex());
    }

    public Player getLastPlayer() {
        // we chose to make the player1 start first every time
        if(moves.isEmpty())
            return null;
        return this.players.get(getLastPlayerIndex());
    }

    public void makeNextMove(Line line) {
        Line lineCandidate = new Line(getCurrentPlayer().getColor(), line.p1(), line.p2());
        Move moveCandidate = new Move(getCurrentPlayer(), new Line(null, lineCandidate).hashCode());
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
                    getLastPlayer().increaseScore();
                    completedBoxes.add(new Point(i,j));
                }
            }
        }
    }

    public List<Player> winner(){
        if (hasEnded()){
            return players.stream()
                    .collect(Collectors.groupingBy(Player::getScore))
                    .entrySet().stream()
                    .max(Comparator.comparingInt(Map.Entry::getKey))
                    .map(Map.Entry::getValue)
                    .orElse(null);
        }
        return null;
    }

}
