package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.core.Color;
import it.units.sdm.dotsandboxes.core.Game;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Player;
import it.units.sdm.dotsandboxes.persistence.Savable;
import it.units.sdm.dotsandboxes.views.IGameView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GameSession implements Savable<GameSession> {

    public record SavedGameSession(String gameControllerClassName, String gameViewClassName, String gameData) {}

    private IGameController controller;
    private IGameView view;
    private Game game;

    GameSession(IGameController controller, IGameView view){
        this.controller = controller;
        this.view = view;
    }

    public GameSession() {
    }

    public void start() {
        if (!controller.initialize()) {
            throw new RuntimeException("Game controller could not initialize!");
        }
        int playerCount = controller.getPlayerCount();
        if (game == null) {
            final List<Player> players = new ArrayList<>();
            managePlayers(playerCount, players);
            game = getGame(players);
        }
        view.init(game.getBoard());
        view.refresh();
        while (!game.hasEnded()) {
            handlePlayerMove(game);
        }
        controller.endGame(game.winners());
    }

    public Game getGame(List<Player> players) {
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

    @Override
    public byte[] save() {
        final String gameData = new String(
                encoder.encode(game.save()), StandardCharsets.UTF_8);
        final String payload = gson.toJson(new SavedGameSession(
                controller.getClass().getName(),
                view.getClass().getName(),
                gameData
        ));
        return payload.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public GameSession restore(byte[] data) {
        final SavedGameSession savedSession = gson.fromJson(new String(data), SavedGameSession.class);
        try {
            this.controller = (IGameController) Class
                    .forName(savedSession.gameControllerClassName)
                    .getConstructor()
                    .newInstance();
            this.view = (IGameView) Class
                    .forName(savedSession.gameViewClassName)
                    .getConstructor()
                    .newInstance();
            this.game = new Game().restore(decoder.decode(savedSession.gameData));
            System.out.println(game);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return this;
    }
}
