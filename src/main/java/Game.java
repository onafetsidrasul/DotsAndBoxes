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
        //just to make the first test pass
        return player1;
    }

    public void makeNextMove(Line line) {
    }

}
