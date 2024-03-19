package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.core.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {
    @Test
    void LinesLongerThan1AreNotAllowed() {
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2, 5, 5);
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0,0,0,2)));
    }

    @Test
    void DiagonalLinesAreNotAllowed() {
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2, 5, 5);
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0,0,1,2)));
    }

    @Test
    void LinesThatStartOutOfBoundsAreNotAllowed(){
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2, 5, 5);
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(6,5,5,5)));
    }

    @Test
    void LinesThatEndOutOfBoundsAreNotAllowed(){
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2, 5, 5);
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0,0,-1,0)));
    }

    @Test
    void OverwritingLinesIsNotAllowed(){
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2, 5, 5);
        testGame.makeNextMove(new Line( 0, 0, 1, 0));
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0,0,1,0)));
    }

    @Test
    void upperLeftBoxIsCompleted() {
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2,5,5);
        testGame.makeNextMove(new Line(0, 0, 1, 0));
        testGame.makeNextMove(new Line(0, 1, 1, 1));
        testGame.makeNextMove(new Line(0, 0, 0, 1));
        testGame.makeNextMove(new Line(1, 0, 1, 1));
        assertTrue(testGame.getGameBoard().isBoxCompleted(new Point(0, 0)));
    }
    @Test
    void player1StartsFirst(){
        Game testGame = new Game(new Player("A", Color.RED), new Player("B", Color.BLUE),5,5);
        assertEquals("A", testGame.getCurrentPlayer().getName());
    }
    @Test
    void playersCorrectlySwitch(){
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2,5,5);
        testGame.makeNextMove(new Line(0,0,0,1));
        assertEquals("B", testGame.getCurrentPlayer().getName());
    }

    @Test
    void TwoBoxesCompletedByTwoPlayers() {
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2,5,5);
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

    @Test
    void boardFullMeansGameHasEnded(){
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2, 5, 5);
        testGame.makeNextMove(new Line( 0, 0, 1, 0));
        testGame.makeNextMove(new Line( 0, 1, 1, 1));
        testGame.makeNextMove(new Line( 0, 0, 0, 1));
        testGame.makeNextMove(new Line( 1, 0, 1, 1));
        testGame.makeNextMove(new Line( 0, 1, 0, 2));
        testGame.makeNextMove(new Line( 0, 2, 1, 2));
        testGame.makeNextMove(new Line( 1, 1, 1, 2));
        testGame.makeNextMove(new Line( 0, 2, 0, 3));
        testGame.makeNextMove(new Line( 0, 3, 1, 3));
        testGame.makeNextMove(new Line( 1, 3, 1, 2));
        testGame.makeNextMove(new Line( 0, 3, 0, 4));
        testGame.makeNextMove(new Line( 0, 4, 1, 4));
        testGame.makeNextMove(new Line( 1, 4, 1, 3));
        testGame.makeNextMove(new Line( 1, 4, 2, 4));
        testGame.makeNextMove(new Line( 2, 4, 2, 3));
        testGame.makeNextMove(new Line( 1, 3, 2, 3));
        testGame.makeNextMove(new Line( 2, 3, 2, 2));
        testGame.makeNextMove(new Line( 1, 2, 2, 2));
        testGame.makeNextMove(new Line( 2, 1, 2, 2));
        testGame.makeNextMove(new Line( 2, 1, 1, 1));
        testGame.makeNextMove(new Line( 2, 1, 2, 0));
        testGame.makeNextMove(new Line( 2, 0, 1, 0));
        testGame.makeNextMove(new Line( 2, 0, 3, 0));
        testGame.makeNextMove(new Line( 3, 0, 3, 1));
        testGame.makeNextMove(new Line( 3, 1, 2, 1));
        testGame.makeNextMove(new Line( 3, 1, 3, 2));
        testGame.makeNextMove(new Line( 3, 2, 2, 2));
        testGame.makeNextMove(new Line( 3, 2, 3, 3));
        testGame.makeNextMove(new Line( 3, 3, 2, 3));
        testGame.makeNextMove(new Line( 3, 4, 2, 4));
        testGame.makeNextMove(new Line( 3, 3, 3, 4));
        testGame.makeNextMove(new Line( 3, 4, 4, 4));
        testGame.makeNextMove(new Line( 4, 4, 4, 3));
        testGame.makeNextMove(new Line( 4, 3, 3, 3));
        testGame.makeNextMove(new Line( 4, 3, 4, 2));
        testGame.makeNextMove(new Line( 4, 2, 3, 2));
        testGame.makeNextMove(new Line( 4, 1, 3, 1));
        testGame.makeNextMove(new Line( 4, 1, 4, 2));
        testGame.makeNextMove(new Line( 4, 1, 4, 0));
        testGame.makeNextMove(new Line( 4, 0, 3, 0));
        assertTrue(testGame.hasEnded());
    }

    @Test
    void tie() {
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2, 5, 5);
        testGame.makeNextMove(new Line( 0, 0, 1, 0));
        testGame.makeNextMove(new Line( 0, 1, 1, 1));
        testGame.makeNextMove(new Line( 0, 0, 0, 1));
        testGame.makeNextMove(new Line( 1, 0, 1, 1));
        testGame.makeNextMove(new Line( 0, 1, 0, 2));
        testGame.makeNextMove(new Line( 0, 2, 1, 2));
        testGame.makeNextMove(new Line( 1, 1, 1, 2));
        testGame.makeNextMove(new Line( 0, 2, 0, 3));
        testGame.makeNextMove(new Line( 0, 3, 1, 3));
        testGame.makeNextMove(new Line( 1, 3, 1, 2));
        testGame.makeNextMove(new Line( 0, 3, 0, 4));
        testGame.makeNextMove(new Line( 0, 4, 1, 4));
        testGame.makeNextMove(new Line( 1, 4, 1, 3));
        testGame.makeNextMove(new Line( 1, 4, 2, 4));
        testGame.makeNextMove(new Line( 2, 4, 2, 3));
        testGame.makeNextMove(new Line( 1, 3, 2, 3));
        testGame.makeNextMove(new Line( 2, 3, 2, 2));
        testGame.makeNextMove(new Line( 1, 2, 2, 2));
        testGame.makeNextMove(new Line( 2, 1, 2, 2));
        testGame.makeNextMove(new Line( 2, 1, 1, 1));
        testGame.makeNextMove(new Line( 2, 1, 2, 0));
        testGame.makeNextMove(new Line( 2, 0, 1, 0));
        testGame.makeNextMove(new Line( 2, 0, 3, 0));
        testGame.makeNextMove(new Line( 3, 0, 3, 1));
        testGame.makeNextMove(new Line( 3, 1, 2, 1));
        testGame.makeNextMove(new Line( 3, 1, 3, 2));
        testGame.makeNextMove(new Line( 3, 2, 2, 2));
        testGame.makeNextMove(new Line( 3, 2, 3, 3));
        testGame.makeNextMove(new Line( 3, 3, 2, 3));
        testGame.makeNextMove(new Line( 3, 4, 2, 4));
        testGame.makeNextMove(new Line( 3, 3, 3, 4));
        testGame.makeNextMove(new Line( 3, 4, 4, 4));
        testGame.makeNextMove(new Line( 4, 4, 4, 3));
        testGame.makeNextMove(new Line( 4, 3, 3, 3));
        testGame.makeNextMove(new Line( 4, 3, 4, 2));
        testGame.makeNextMove(new Line( 4, 2, 3, 2));
        testGame.makeNextMove(new Line( 4, 1, 3, 1));
        testGame.makeNextMove(new Line( 4, 1, 4, 2));
        testGame.makeNextMove(new Line( 4, 1, 4, 0));
        testGame.makeNextMove(new Line( 4, 0, 3, 0));
        assertEquals(List.of(player1, player2), testGame.winner());
    }

    @Test
    void player1wins() {
        Player player1 = new Player("A", Color.RED);
        Player player2 = new Player("B", Color.BLUE);
        Game testGame = new Game(player1, player2, 5, 5);
        testGame.makeNextMove(new Line( 0, 0, 1, 0));
        testGame.makeNextMove(new Line( 0, 1, 1, 1));
        testGame.makeNextMove(new Line( 0, 0, 0, 1));
        testGame.makeNextMove(new Line( 1, 0, 1, 1));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 0, 1, 0, 2));
        testGame.makeNextMove(new Line( 0, 2, 1, 2));
        testGame.makeNextMove(new Line( 1, 1, 1, 2));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 0, 2, 0, 3));
        testGame.makeNextMove(new Line( 0, 3, 1, 3));
        testGame.makeNextMove(new Line( 1, 3, 1, 2));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 0, 3, 0, 4));
        testGame.makeNextMove(new Line( 0, 4, 1, 4));
        testGame.makeNextMove(new Line( 1, 4, 1, 3));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 1, 4, 2, 4));
        testGame.makeNextMove(new Line( 2, 4, 2, 3));
        testGame.makeNextMove(new Line( 1, 3, 2, 3));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 2, 3, 2, 2));
        testGame.makeNextMove(new Line( 1, 2, 2, 2));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 2, 1, 2, 2));
        testGame.makeNextMove(new Line( 2, 1, 1, 1));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 2, 1, 2, 0));
        testGame.makeNextMove(new Line( 2, 0, 1, 0));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 2, 0, 3, 0));
        testGame.makeNextMove(new Line( 3, 0, 3, 1));
        testGame.makeNextMove(new Line( 3, 1, 2, 1));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 3, 1, 3, 2));
        testGame.makeNextMove(new Line( 3, 2, 2, 2));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 3, 2, 3, 3));
        testGame.makeNextMove(new Line( 3, 3, 2, 3));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 3, 4, 2, 4));
        testGame.makeNextMove(new Line( 3, 3, 3, 4));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 3, 4, 4, 4));
        testGame.makeNextMove(new Line( 4, 0, 3, 0));
        testGame.makeNextMove(new Line( 4, 1, 4, 0));
        testGame.makeNextMove(new Line( 4, 1, 3, 1));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 4, 1, 4, 2));
        testGame.makeNextMove(new Line( 4, 2, 3, 2));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 4, 3, 4, 2));
        testGame.makeNextMove(new Line( 4, 3, 3, 3));
        testGame.updateScore();
        testGame.makeNextMove(new Line( 4, 4, 4, 3));
        testGame.updateScore();
        assertEquals(List.of(player1), testGame.winner());
    }

    @Test
    void equals() {
        Line a = new Line(0, 1, 0,0);
        Line b = new Line(0, 0, 0,1);
        if (Board.isNotNormalized(a)){
            a=Board.normalizer(a);
        }
        if (Board.isNotNormalized(b)){
            b=Board.normalizer(b);
        }
        assertEquals(a,b);
    }
}