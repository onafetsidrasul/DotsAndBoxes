import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Game {

    Player player1, player2;
    Board gameBoard;
    ArrayList<Move> moves;
    List<int[]> completedBoxes = new ArrayList<>();

    public Game(Player player1, Player player2) {
        this.player1 = Objects.requireNonNull(player1);
        this.player2 = Objects.requireNonNull(player2);
        this.gameBoard = new Board(5, 5);   // fixed dimension for now, add parametrized version later
        this.moves = new ArrayList<>();
    }

    public Player getCurrentPlayer() {
        // we chose to make the player1 start first every time
        if(moves.isEmpty())
            return this.player1;
        else return moves.get(moves.size()-1).player().equals(this.player1) ? this.player2 : this.player1;
    }

    public Player getLastPlayer(){
        if (getCurrentPlayer()==this.player1)
                return this.player2;
        else return this.player1;
    }

    public void makeNextMove(Line line) {
        Line lineCandidate = new Line(getCurrentPlayer().getColor(), line.x1(), line.y1(), line.x2(), line.y2());
        Move moveCandidate = new Move(getCurrentPlayer(), new Line(null,lineCandidate).hashCode());
        gameBoard.addLine(lineCandidate);
        moves.add(moveCandidate);
    }

    public void updateScore() {
        for (int i = 0; i < gameBoard.getX_dimension(); i++) {
            for (int j = 0; j < gameBoard.getY_dimension(); j++) {
                if (gameBoard.isBoxCompleted(i, j) && !containsCoordinates(completedBoxes, i, j)) {
                    getLastPlayer().increaseScore();
                    completedBoxes.add(new int[]{i, j});
                }
            }
        }
    }

    private boolean containsCoordinates(List<int[]> list, int x, int y) {
        for (int[] coordinates : list) {
            if (coordinates[0] == x && coordinates[1] == y) {
                return true;
            }
        }
        return false;
    }
}
