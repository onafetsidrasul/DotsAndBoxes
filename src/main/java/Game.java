import java.util.ArrayList;

public class Game {

    Player player1, player2;
    Board gameBoard;
    ArrayList<Line> moves;

    public Game(Player player1, Player player2){
        this.player1 = player1;
        this.player2 = player2;
        this.gameBoard = new Board();
        this.moves = new ArrayList<>();
    }

    public Player getCurrentPlayer(){

    }

    public void makeNextMove(Line line){

    }

}
