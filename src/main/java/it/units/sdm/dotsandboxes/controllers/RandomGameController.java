package it.units.sdm.dotsandboxes.controllers;

import it.units.sdm.dotsandboxes.core.Board;
import it.units.sdm.dotsandboxes.core.Color;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomGameController implements IGameController {

    private final Set<Line> drawnLines = new HashSet<>();
    private static final Random rnd = new Random();

    @Override
    public boolean initialize() {
        System.out.println("|=.=.=.=.=.=.=.=.=.=.=.=.=.|");
        System.out.println("|.==== DOTS and BOXES =====|");
        System.out.println("|==== terminal version ===.|");
        System.out.println("|.=.=.=.=.=.=.=.=.=.=.=.=.=|");
        return true;
    }

    @Override
    public int getPlayerCount() {
        return 2;
    }

    @Override
    public String getPlayerName(int playerNumber, Color color) {
        return "Giocatore " + playerNumber;
    }

    @Override
    public int[] getBoardDimensions() {
        return new int[] { 5, 5 };
    }

    @Override
    public void updateBoard(Board board) {

    }

    @Override
    public void updatePlayer(Player player) {

    }

    @Override
    public Line waitForLine(Player unused) {
        int[] dims = getBoardDimensions();
        int x1, y1, x2, y2;
        Line candidate;
        do {
            x1 = (int) Math.floor(Math.random() * dims[0]);
            y1 = (int) Math.floor(Math.random() * dims[1]);
            do {

                x2 = x1 + (Math.random() >= 0.5 ? 1 : -1);
                y2 = y1 + (Math.random() >= 0.5 ? 1 : -1);
            } while (x2 < 0 || x2 >= dims[0] || y2 < 0 || y2 >= dims[1]);
            candidate = new Line(x1, y1, x2, y2);
            System.out.println(candidate);
        } while (drawnLines.contains(candidate));
        drawnLines.add(candidate);
        return candidate;
    }

    @Override
    public void endGame(List<Player> winner) {

    }

}