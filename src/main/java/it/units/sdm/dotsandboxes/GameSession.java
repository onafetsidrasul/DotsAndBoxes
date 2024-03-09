package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.core.Color;
import it.units.sdm.dotsandboxes.core.Game;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;

import java.util.ArrayList;
import java.util.List;

public class GameSession {

    public static void start(IGameController controller) {
        if (!controller.initialize()) {
            throw new RuntimeException("Game controller could not initialize!");
        }
        int playerCount = controller.getPlayerCount();
        final List<Player> players = new ArrayList<>();
        managePlayers(controller, playerCount, players);
        final Game game = getGame(controller, players);
        while (!game.hasEnded()) {
            handlePlayerMove(controller, game);
        }
        controller.endGame(game.winner());
    }

    private static Game getGame(IGameController controller, List<Player> players) {
        int[] dimensions = controller.getBoardDimensions();
        final Game game = new Game(players, dimensions[0], dimensions[1] );
        controller.updateBoard(game.getGameBoard());
        return game;
    }

    private static void managePlayers(IGameController controller, int playerCount, List<Player> players) {
        for (int playerNumber = 1; playerNumber < playerCount + 1; playerNumber++) {
            final Color playerColor = Color.values()[playerNumber % Color.values().length];
            players.add(new Player(controller.getPlayerName(playerNumber), playerColor));
        }
    }

    private static void handlePlayerMove(IGameController controller, Game game) {
        final Line line = controller.waitForLine(game.getCurrentPlayer());
        try {
            game.makeNextMove(new Line(line.p1().x(), line.p1().y(), line.p2().x(), line.p2().y()));
            game.updateScore();
        } catch (RuntimeException e) {
            System.err.println("Exception: " + e.getMessage());
        }
        controller.updatePlayer(game.getLastPlayer());
        controller.updateBoard(game.getGameBoard());
    }

}
