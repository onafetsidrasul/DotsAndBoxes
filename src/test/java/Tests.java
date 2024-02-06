import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {
    @Test
    void DoNotAllowLinesLongerThan1() { assertThrows(Exception.class, () -> new Line(0, 0, 0, 2));
    }

    @Test
    void DoNotAllowDiagonalLines() { assertThrows(Exception.class, () -> new Line(0, 0, 1, 1));
    }

    @Test
    void BoxCompletedTrue() {
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Board board = new Board(5,5);
        board.addMove(new Line(player1, 0, 0, 1, 0));
        board.addMove(new Line(player2, 1, 0, 1, 1));
        board.addMove(new Line(player1, 1, 1, 0, 1));
        board.addMove(new Line(player2, 0, 1, 0, 0));
        assertTrue(board.checkCompletedBox(0, 0));
    }

    @Test
    void player1StartsFirst(){
        Game testGame = new Game(new Player("p1", Color.RED), new Player("p2", Color.BLUE));
        assertEquals("p1", testGame.getCurrentPlayer().getName());
    }
    @Test
    void playersCorrectlySwitch(){
        Game testGame = new Game(new Player("p1", Color.RED), new Player("p2", Color.BLUE));
        testGame.makeNextMove(new Line(0,0,0,1));
        assertEquals("p2", testGame.getCurrentPlayer().getName());
    }
}