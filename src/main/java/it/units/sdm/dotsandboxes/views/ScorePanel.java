package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.core.Game;

import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {
    private final JLabel scoreLabels;
    private final JLabel turnLabel;

    public ScorePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        scoreLabels = new JLabel();
        turnLabel = new JLabel("Turn: Player 1");
        add(scoreLabels);
        add(turnLabel);
    }

    public void updateScore(Game game) {
        StringBuilder scores = new StringBuilder("<html>");
        for (int i = 1; i <= game.players.size(); i++) {
            String player = game.players.get(i - 1);
            String score = String.valueOf(game.scoreBoard.get(player));
            scores.append(player).append(": ").append(score).append("<br>");
        }
        scores.append("</html>");
        scoreLabels.setText(scores.toString());
    }

    public void updateTurn(Game game) {
        String currentPlayer = game.currentPlayer();
        turnLabel.setText("Current player: " + currentPlayer);
        Color playerColor = game.playerColorLUT.get(currentPlayer).toAwtColor();
        turnLabel.setForeground(playerColor);
    }
}
