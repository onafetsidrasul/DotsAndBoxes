package it.units.sdm.dotsandboxes.views;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class SwingView extends IGameView implements Runnable {

    private JFrame frame;
    private JPanel mainPanel;
    private BoardPanel boardPanel;
    private ScorePanel scorePanel;

    @Override
    protected boolean finishInit() {
        try {
            frame = new JFrame("Dots and Boxes");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            mainPanel = new JPanel(new BorderLayout());
            frame.add(mainPanel);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected boolean finishConfigure() {
        try {
            boardPanel = new BoardPanel(gameStateReference, controllerReference);
            scorePanel = new ScorePanel();

            mainPanel.add(boardPanel, BorderLayout.CENTER);
            mainPanel.add(scorePanel, BorderLayout.EAST);

            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void displayGameUI() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
        new Thread(this).start();
    }

    @Override
    public String promptForGamemode() {
        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose the game mode.",
                "",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Against players", "Against computer"},
                "Against players"
        );
        return switch (choice) {
            case 0 -> "PVP";
            case 1 -> "PVE";
            default -> null;
        };
    }

    @Override
    public String promptForNumberOfPlayers() {
        return JOptionPane.showInputDialog("Insert the number of players");
    }

    @Override
    public String promptForBoardHeight() {
        return JOptionPane.showInputDialog("Insert the height of the board");
    }

    @Override
    public String promptForBoardWidth() {
        return JOptionPane.showInputDialog("Insert the width of the board");
    }

    @Override
    public String promptForPlayerName(int playerNumber) {
        return JOptionPane.showInputDialog("Insert the name of player " + playerNumber);
    }

    @Override
    public String promptForPostGameIntent() {
        int choice = JOptionPane.showOptionDialog(
                null,
                "What do you want to do?",
                "",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"PLAY AGAIN", "EXIT"},
                "EXIT"
        );
        return switch (choice) {
            case 0 -> "NEW";
            case 1 -> "END";
            default -> "END";
        };
    }

    @Override
    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    @Override
    public void displayWarning(String message) {
        JOptionPane.showOptionDialog(
                null,
                message,
                "WARNING",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                null,
                null
        );
    }

    @Override
    public void displayResults() {
        JOptionPane.showMessageDialog(
                null,
                "GAME RESULTS\n" + gameStateReference.scoreBoard
        );
    }

    @Override
    public void run() {
        while (true) {
            try {
                controllerReference.refreshUISem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                controllerReference.gameOverCheckSem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (controllerReference.gameIsOver) {
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        boardPanel.repaint();
                        mainPanel.revalidate();
                        mainPanel.repaint();
                    });

                    // Slight delay to ensure the last line is drawn
                    Thread.sleep(1000);
                } catch (InterruptedException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                SwingUtilities.invokeLater(() -> frame.dispose());
                break;
            }
            SwingUtilities.invokeLater(this::refreshBoardComponents);
        }
    }

    private void refreshBoardComponents() {
        synchronized (gameStateReference.board) {
            boardPanel.repaint();
            mainPanel.revalidate();
            mainPanel.repaint();
            scorePanel.updateTurn(gameStateReference);
            scorePanel.updateScore(gameStateReference);
        }
        isRefreshingUISem.release();
    }
}
