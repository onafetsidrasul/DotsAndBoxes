package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.core.Color;
import it.units.sdm.dotsandboxes.core.Game;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;
import it.units.sdm.dotsandboxes.views.IGameView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameSession {

    private final IGameController controller;
    private final IGameView view;
    private Game game;

    GameSession(IGameController controller, IGameView view){
        this.controller=controller;
        this.view=view;
    }

    public void start() {
        if (!controller.initialize()) {
            throw new RuntimeException("Game controller could not initialize!");
        }
        int playerCount = controller.getPlayerCount();
        final List<Player> players = new ArrayList<>();
        managePlayers(playerCount, players);
        game = getGame(players);
        view.init(game.getBoard());
        view.refresh();
        while (!game.hasEnded()) {
            handlePlayerMove(game);
        }
        controller.endGame(winner(players));
    }

    public static List<Player> winner(List<Player> players) {
        return players.stream()
                .collect(Collectors.groupingBy(Player::score))
                .entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    private Game getGame(List<Player> players) {
        int[] dimensions = controller.getBoardDimensions();
        return new Game(dimensions[0], dimensions[1], players);
    }

    private void managePlayers(int playerCount, List<Player> players) {
        for (int playerNumber = 1; playerNumber < playerCount + 1; playerNumber++) {
            final Color playerColor = Color.values()[playerNumber % Color.values().length];
            players.add(new Player(controller.getPlayerName(playerNumber), playerColor));
        }
    }

    private void handlePlayerMove(Game game) {
        final Line line = controller.waitForLine(game.getCurrentPlayer());
        try {
            game.makeNextMove(new Line(line.p1().x(), line.p1().y(), line.p2().x(), line.p2().y()));
            game.updateScore();
        } catch (RuntimeException e) {
            System.err.println("Exception: " + e.getMessage());
        }
        controller.updatePlayer(game.getLastPlayer());
        view.refresh();
    }

}
