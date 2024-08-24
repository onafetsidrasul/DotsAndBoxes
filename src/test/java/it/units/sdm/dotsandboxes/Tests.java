package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.core.*;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {

    private final String player1Name = "A";
    private final String player2Name = "B";
    private final int boardHeight = 5;
    private final int boardWidth = 5;

    @Test
    void linesLongerThan1AreNotAllowed() throws InvalidInputException {
        Game testGame = new Game(boardHeight, boardWidth, player1Name, player2Name);
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0, 0, 0, 2)));
    }

    @Test
    void diagonalLinesAreNotAllowed() throws InvalidInputException {
        Game testGame = new Game(boardHeight, boardWidth, player1Name, player2Name);
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0, 0, 1, 2)));
    }

    @Test
    void linesThatStartOutOfBoundsAreNotAllowed() throws InvalidInputException {
        Game testGame = new Game(boardHeight, boardWidth, player1Name, player2Name);
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(6, 5, 5, 5)));
    }

    @Test
    void linesThatEndOutOfBoundsAreNotAllowed() throws InvalidInputException {
        Game testGame = new Game(boardHeight, boardWidth, player1Name, player2Name);
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0, 0, -1, 0)));
    }

    @Test
    void overwritingLinesIsNotAllowed() throws InvalidInputException {
        Game testGame = new Game(boardHeight, boardWidth, player1Name, player2Name);
        testGame.makeNextMove(new Line(0, 0, 1, 0));
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0, 0, 1, 0)));
    }

    @Test
    void upperLeftBoxIsCompleted() throws InvalidInputException {
        Game testGame = new Game(boardHeight, boardWidth, player1Name, player2Name);
        testGame.makeNextMove(new Line(0, 0, 1, 0));
        testGame.makeNextMove(new Line(0, 1, 1, 1));
        testGame.makeNextMove(new Line(0, 0, 0, 1));
        testGame.makeNextMove(new Line(1, 0, 1, 1));
        assertTrue(testGame.board().isBoxCompleted(new Point(0, 0)));
    }

    @Test
    void player1StartsFirst() throws InvalidInputException {
        Game testGame = new Game(boardHeight, boardWidth, player1Name, player2Name);
        assertEquals("A", testGame.currentPlayer());
    }

    @Test
    void playersCorrectlySwitch() throws InvalidInputException {
        Game testGame = new Game(boardHeight, boardWidth, player1Name, player2Name);
        testGame.makeNextMove(new Line(0, 0, 0, 1));
        assertEquals("B", testGame.currentPlayer());
    }

    @Test
    void twoBoxesCompletedByTwoPlayers() throws InvalidInputException {
        Game testGame = new Game(boardHeight, boardWidth, player1Name, player2Name);
        testGame.makeNextMove(new Line(0, 0, 1, 0));
        testGame.makeNextMove(new Line(0, 1, 1, 1));
        testGame.makeNextMove(new Line(0, 0, 0, 1));
        testGame.makeNextMove(new Line(1, 0, 1, 1));
        testGame.updateScore();
        testGame.makeNextMove(new Line(0, 1, 0, 2));
        testGame.makeNextMove(new Line(0, 2, 1, 2));
        testGame.makeNextMove(new Line(1, 1, 1, 2));
        testGame.updateScore();
        assertEquals(1, testGame.getPlayerScore("A"));
        assertEquals(1, testGame.getPlayerScore("B"));
    }

    @Test
    void boardFullMeansGameHasEnded() throws InvalidInputException {
        Game testGame = new Game(boardHeight, boardWidth, player1Name, player2Name);
        testGame.makeNextMove(new Line(0, 0, 1, 0));
        testGame.makeNextMove(new Line(0, 1, 1, 1));
        testGame.makeNextMove(new Line(0, 0, 0, 1));
        testGame.makeNextMove(new Line(1, 0, 1, 1));
        testGame.makeNextMove(new Line(0, 1, 0, 2));
        testGame.makeNextMove(new Line(0, 2, 1, 2));
        testGame.makeNextMove(new Line(1, 1, 1, 2));
        testGame.makeNextMove(new Line(0, 2, 0, 3));
        testGame.makeNextMove(new Line(0, 3, 1, 3));
        testGame.makeNextMove(new Line(1, 3, 1, 2));
        testGame.makeNextMove(new Line(0, 3, 0, 4));
        testGame.makeNextMove(new Line(0, 4, 1, 4));
        testGame.makeNextMove(new Line(1, 4, 1, 3));
        testGame.makeNextMove(new Line(1, 4, 2, 4));
        testGame.makeNextMove(new Line(2, 4, 2, 3));
        testGame.makeNextMove(new Line(1, 3, 2, 3));
        testGame.makeNextMove(new Line(2, 3, 2, 2));
        testGame.makeNextMove(new Line(1, 2, 2, 2));
        testGame.makeNextMove(new Line(2, 1, 2, 2));
        testGame.makeNextMove(new Line(2, 1, 1, 1));
        testGame.makeNextMove(new Line(2, 1, 2, 0));
        testGame.makeNextMove(new Line(2, 0, 1, 0));
        testGame.makeNextMove(new Line(2, 0, 3, 0));
        testGame.makeNextMove(new Line(3, 0, 3, 1));
        testGame.makeNextMove(new Line(3, 1, 2, 1));
        testGame.makeNextMove(new Line(3, 1, 3, 2));
        testGame.makeNextMove(new Line(3, 2, 2, 2));
        testGame.makeNextMove(new Line(3, 2, 3, 3));
        testGame.makeNextMove(new Line(3, 3, 2, 3));
        testGame.makeNextMove(new Line(3, 4, 2, 4));
        testGame.makeNextMove(new Line(3, 3, 3, 4));
        testGame.makeNextMove(new Line(3, 4, 4, 4));
        testGame.makeNextMove(new Line(4, 4, 4, 3));
        testGame.makeNextMove(new Line(4, 3, 3, 3));
        testGame.makeNextMove(new Line(4, 3, 4, 2));
        testGame.makeNextMove(new Line(4, 2, 3, 2));
        testGame.makeNextMove(new Line(4, 1, 3, 1));
        testGame.makeNextMove(new Line(4, 1, 4, 2));
        testGame.makeNextMove(new Line(4, 1, 4, 0));
        testGame.makeNextMove(new Line(4, 0, 3, 0));
        assertTrue(testGame.hasEnded());
    }

    @Test
    void moreThanOneWinnerWhenGameTies() throws InvalidInputException {
        Game testGame = new Game(boardHeight, boardWidth, player1Name, player2Name);
        testGame.makeNextMove(new Line(0, 0, 1, 0));
        testGame.makeNextMove(new Line(0, 1, 1, 1));
        testGame.makeNextMove(new Line(0, 0, 0, 1));
        testGame.makeNextMove(new Line(1, 0, 1, 1));
        testGame.makeNextMove(new Line(0, 1, 0, 2));
        testGame.makeNextMove(new Line(0, 2, 1, 2));
        testGame.makeNextMove(new Line(1, 1, 1, 2));
        testGame.makeNextMove(new Line(0, 2, 0, 3));
        testGame.makeNextMove(new Line(0, 3, 1, 3));
        testGame.makeNextMove(new Line(1, 3, 1, 2));
        testGame.makeNextMove(new Line(0, 3, 0, 4));
        testGame.makeNextMove(new Line(0, 4, 1, 4));
        testGame.makeNextMove(new Line(1, 4, 1, 3));
        testGame.makeNextMove(new Line(1, 4, 2, 4));
        testGame.makeNextMove(new Line(2, 4, 2, 3));
        testGame.makeNextMove(new Line(1, 3, 2, 3));
        testGame.makeNextMove(new Line(2, 3, 2, 2));
        testGame.makeNextMove(new Line(1, 2, 2, 2));
        testGame.makeNextMove(new Line(2, 1, 2, 2));
        testGame.makeNextMove(new Line(2, 1, 1, 1));
        testGame.makeNextMove(new Line(2, 1, 2, 0));
        testGame.makeNextMove(new Line(2, 0, 1, 0));
        testGame.makeNextMove(new Line(2, 0, 3, 0));
        testGame.makeNextMove(new Line(3, 0, 3, 1));
        testGame.makeNextMove(new Line(3, 1, 2, 1));
        testGame.makeNextMove(new Line(3, 1, 3, 2));
        testGame.makeNextMove(new Line(3, 2, 2, 2));
        testGame.makeNextMove(new Line(3, 2, 3, 3));
        testGame.makeNextMove(new Line(3, 3, 2, 3));
        testGame.makeNextMove(new Line(3, 4, 2, 4));
        testGame.makeNextMove(new Line(3, 3, 3, 4));
        testGame.makeNextMove(new Line(3, 4, 4, 4));
        testGame.makeNextMove(new Line(4, 4, 4, 3));
        testGame.makeNextMove(new Line(4, 3, 3, 3));
        testGame.makeNextMove(new Line(4, 3, 4, 2));
        testGame.makeNextMove(new Line(4, 2, 3, 2));
        testGame.makeNextMove(new Line(4, 1, 3, 1));
        testGame.makeNextMove(new Line(4, 1, 4, 2));
        testGame.makeNextMove(new Line(4, 1, 4, 0));
        testGame.makeNextMove(new Line(4, 0, 3, 0));
        assertEquals(List.of("A", "B"), testGame.winners());
    }

    @Test
    void aPlayerCanWin() throws InvalidInputException {
        Game testGame = new Game(boardHeight, boardWidth, player1Name, player2Name);
        testGame.makeNextMove(new Line(0, 0, 1, 0));
        testGame.makeNextMove(new Line(0, 1, 1, 1));
        testGame.makeNextMove(new Line(0, 0, 0, 1));
        testGame.makeNextMove(new Line(1, 0, 1, 1));
        testGame.updateScore();
        testGame.makeNextMove(new Line(0, 1, 0, 2));
        testGame.makeNextMove(new Line(0, 2, 1, 2));
        testGame.makeNextMove(new Line(1, 1, 1, 2));
        testGame.updateScore();
        testGame.makeNextMove(new Line(0, 2, 0, 3));
        testGame.makeNextMove(new Line(0, 3, 1, 3));
        testGame.makeNextMove(new Line(1, 3, 1, 2));
        testGame.updateScore();
        testGame.makeNextMove(new Line(0, 3, 0, 4));
        testGame.makeNextMove(new Line(0, 4, 1, 4));
        testGame.makeNextMove(new Line(1, 4, 1, 3));
        testGame.updateScore();
        testGame.makeNextMove(new Line(1, 4, 2, 4));
        testGame.makeNextMove(new Line(2, 4, 2, 3));
        testGame.makeNextMove(new Line(1, 3, 2, 3));
        testGame.updateScore();
        testGame.makeNextMove(new Line(2, 3, 2, 2));
        testGame.makeNextMove(new Line(1, 2, 2, 2));
        testGame.updateScore();
        testGame.makeNextMove(new Line(2, 1, 2, 2));
        testGame.makeNextMove(new Line(2, 1, 1, 1));
        testGame.updateScore();
        testGame.makeNextMove(new Line(2, 1, 2, 0));
        testGame.makeNextMove(new Line(2, 0, 1, 0));
        testGame.updateScore();
        testGame.makeNextMove(new Line(2, 0, 3, 0));
        testGame.makeNextMove(new Line(3, 0, 3, 1));
        testGame.makeNextMove(new Line(3, 1, 2, 1));
        testGame.updateScore();
        testGame.makeNextMove(new Line(3, 1, 3, 2));
        testGame.makeNextMove(new Line(3, 2, 2, 2));
        testGame.updateScore();
        testGame.makeNextMove(new Line(3, 2, 3, 3));
        testGame.makeNextMove(new Line(3, 3, 2, 3));
        testGame.updateScore();
        testGame.makeNextMove(new Line(3, 4, 2, 4));
        testGame.makeNextMove(new Line(3, 3, 3, 4));
        testGame.updateScore();
        testGame.makeNextMove(new Line(3, 4, 4, 4));
        testGame.makeNextMove(new Line(4, 0, 3, 0));
        testGame.makeNextMove(new Line(4, 1, 4, 0));
        testGame.makeNextMove(new Line(4, 1, 3, 1));
        testGame.updateScore();
        testGame.makeNextMove(new Line(4, 1, 4, 2));
        testGame.makeNextMove(new Line(4, 2, 3, 2));
        testGame.updateScore();
        testGame.makeNextMove(new Line(4, 3, 4, 2));
        testGame.makeNextMove(new Line(4, 3, 3, 3));
        testGame.updateScore();
        testGame.makeNextMove(new Line(4, 4, 4, 3));
        testGame.updateScore();
        assertEquals(List.of("A"), testGame.winners());
    }

    @Test
    void linesAreEqualDespiteEndpointsOrder() throws InvalidInputException {
        Line a = new Line(0, 1, 0, 0);
        Line b = new Line(0, 0, 0, 1);
        assertEquals(a, b);
    }
}