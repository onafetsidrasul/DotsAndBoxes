package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.core.*;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {
    @Test
    void LinesLongerThan1AreNotAllowed() throws InvalidInputException {
        Game testGame = new Game(5, 5, "A", "B");
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0, 0, 0, 2)));
    }

    @Test
    void DiagonalLinesAreNotAllowed() throws InvalidInputException {
        Game testGame = new Game(5, 5, "A", "B");
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0, 0, 1, 2)));
    }

    @Test
    void LinesThatStartOutOfBoundsAreNotAllowed() throws InvalidInputException {
        Game testGame = new Game(5, 5, "A", "B");
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(6, 5, 5, 5)));
    }

    @Test
    void LinesThatEndOutOfBoundsAreNotAllowed() throws InvalidInputException {
        Game testGame = new Game(5, 5, "A", "B");
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0, 0, -1, 0)));
    }

    @Test
    void OverwritingLinesIsNotAllowed() throws InvalidInputException {
        Game testGame = new Game(5, 5, "A", "B");
        testGame.makeNextMove(new Line(0, 0, 1, 0));
        assertThrows(Exception.class, () -> testGame.makeNextMove(new Line(0, 0, 1, 0)));
    }

    @Test
    void UpperLeftBoxIsCompleted() throws InvalidInputException {
        Game testGame = new Game(5, 5, "A", "B");
        testGame.makeNextMove(new Line(0, 0, 1, 0));
        testGame.makeNextMove(new Line(0, 1, 1, 1));
        testGame.makeNextMove(new Line(0, 0, 0, 1));
        testGame.makeNextMove(new Line(1, 0, 1, 1));
        assertTrue(testGame.board().isBoxCompleted(new Point(0, 0)));
    }

    @Test
    void Player1StartsFirst() throws InvalidInputException {
        Game testGame = new Game(5, 5, "A", "B");
        assertEquals("A", testGame.getCurrentPlayer());
    }

    @Test
    void PlayersCorrectlySwitch() throws InvalidInputException {
        Game testGame = new Game(5, 5, "A", "B");
        testGame.makeNextMove(new Line(0, 0, 0, 1));
        assertEquals("B", testGame.getCurrentPlayer());
    }

    @Test
    void TwoBoxesCompletedByTwoPlayersMeansTheirScoresAre1() throws InvalidInputException {
        Game testGame = new Game(5, 5, "A", "B");
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
    void BoardFullMeansGameHasEnded() throws InvalidInputException {
        Game testGame = new Game(5, 5, "A", "B");
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
    void TieMeansBothPlayersAreWinners() throws InvalidInputException {
        Game testGame = new Game(5, 5, "A", "B");
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
    void Player1wins() throws InvalidInputException {
        Game testGame = new Game(5, 5, "A", "B");
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
    void LinesWithSwappedEndpointsAreEqual() throws InvalidInputException {
        Line a = new Line(0, 1, 0, 0);
        Line b = new Line(0, 0, 0, 1);
        assertEquals(a, b);
    }
}