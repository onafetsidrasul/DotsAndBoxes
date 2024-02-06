import java.util.ArrayList;
import java.util.Objects;

public class Game {

    Player player1, player2;
    Board gameBoard;
    ArrayList<Line> moves;

    public Game(Player player1, Player player2) {
        this.player1 = Objects.requireNonNull(player1);
        this.player2 = Objects.requireNonNull(player2);
        this.gameBoard = new Board();
        this.moves = new ArrayList<>();
    }

    public Player getCurrentPlayer() {
        // we chose to make the player1 start first every time
        if(moves.isEmpty())
            return this.player1;
        else return moves.get(moves.size()-1).player().equals(this.player1) ? this.player2 : this.player1;
    }

    public void makeNextMove(Line line) {
        Line move = new Line(getCurrentPlayer(), line.x1(), line.y1(), line.x2(), line.x2());
        gameBoard.addMove(move);
        moves.add(move);
    }

}
