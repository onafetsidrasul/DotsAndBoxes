import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {
    @Test
    void LinesLongerThan1AreNotAllowed() {
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2);
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0,0,0,2)));
    }

    @Test
    void DiagonalLinesAreNotAllowed() {
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2);
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0,0,1,2)));
    }

    @Test
    void LinesThatStartOutOfBoundsAreNotAllowed(){
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2);
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(6,5,5,5)));
    }

    @Test
    void LinesThatEndOutOfBoundsAreNotAllowed(){
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2);
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0,0,-1,0)));
    }

    @Test
    void OverwritingLinesIsNotAllowed(){
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2);
        testGame.makeNextMove(new Line( 0, 0, 1, 0));
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0,0,1,0)));
    }

    @Test
    void upperLeftBoxIsCompleted() {
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2);
        testGame.makeNextMove(new Line(0, 0, 1, 0));
        testGame.makeNextMove(new Line(0, 1, 1, 1));
        testGame.makeNextMove(new Line(0, 0, 0, 1));
        testGame.makeNextMove(new Line(1, 0, 1, 1));
        assertTrue(testGame.gameBoard.isBoxCompleted(new Point(0,0)));
    }
    @Test
    void player1StartsFirst(){
        Game testGame = new Game(new Player("A", Color.RED), new Player("B", Color.BLUE));
        assertEquals("A", testGame.getCurrentPlayer().getName());
    }
    @Test
    void playersCorrectlySwitch(){
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2);
        testGame.makeNextMove(new Line(0,0,0,1));
        assertEquals("B", testGame.getCurrentPlayer().getName());
    }

    @Test
    void TwoBoxesCompletedByTwoPlayers() {
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2);
        testGame.makeNextMove(new Line( 0, 0, 1, 0));
        testGame.makeNextMove(new Line( 0, 1, 1, 1));
        testGame.makeNextMove(new Line( 0, 0, 0, 1));
        testGame.makeNextMove(new Line( 1, 0, 1, 1));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 0, 1, 0, 2));
        testGame.makeNextMove(new Line( 0, 2, 1, 2));
        testGame.makeNextMove(new Line( 1, 1, 1, 2));
        testGame.updateScore();
        assertEquals(1, player1.getScore());
        assertEquals(1, player2.getScore());
    }

}