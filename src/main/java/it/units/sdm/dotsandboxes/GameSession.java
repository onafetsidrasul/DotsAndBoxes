package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.core.Color;
import it.units.sdm.dotsandboxes.core.Game;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameSession {

    public static void start(IGameController controller) {
        if (!controller.initialize()) {
            throw new RuntimeException("Game controller could not initialize!");
        }

        int playerCount = controller.getPlayerCount();
        final List<Player> players = new ArrayList<>();
        for (int playerNumber = 1; playerNumber < playerCount + 1; playerNumber++) {
            final Color playerColor = Color.values()[playerNumber % Color.values().length];
            players.add(new Player(controller.getPlayerName(playerNumber, playerColor), playerColor));
        }
        int[] dimensions = controller.getBoardDimensions();

        final Game game = new Game(dimensions, players);
        while (!game.hasEnded()) {
            final Line line = controller.waitForLine(game.getCurrentPlayer());
            try {
                game.makeNextMove(new Line(line.x1(), line.y1(), line.x2(), line.y2()));
            } catch (RuntimeException e) {
                continue;
            }
            controller.updatePlayer(game.getCurrentPlayer());
            /* FIXME: should we be passing the board object for updating the board UI? */
            controller.updateBoard(game.getGameBoard());
        }
        controller.endGame(players.stream()
                .max(Comparator.comparingInt(Player::getScore))
                .orElseThrow());
    }

}
