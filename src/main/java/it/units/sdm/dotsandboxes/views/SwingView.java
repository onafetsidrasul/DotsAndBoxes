package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.core.Point;

import javax.swing.*;
import java.awt.*;

public class SwingView extends IGameView {

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel boardPanel;
    private JPanel scorePanel;
    private boolean firstEndpointIsSelected = false;


    @Override
    protected boolean finishInit() {
        try {
            frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            mainPanel = new JPanel(new GridLayout(1, 2));
            frame.add(mainPanel);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected boolean finishConfigure() {
        try {
            boardPanel = new JPanel(new GridLayout(2 * gameStateReference.board.height() - 1, 2 * gameStateReference.board.width() - 1));
            scorePanel = new JPanel(new GridLayout(gameStateReference.players.size(), 1));
            mainPanel.add(boardPanel);
            mainPanel.add(scorePanel);
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    @Override
    protected void displayGameUI() {
        SwingUtilities.invokeLater(this);
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
        frame.setVisible(true);
        do {
            try {
                controllerReference.readyToRefreshUISem.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(controllerReference.gameIsOver){
                break;
            }
            refreshBoardComponents();
        } while (true);
    }

    private void refreshBoardComponents() {
        boardPanel.removeAll();
        synchronized (gameStateReference.board) {
            for (int i = 0; i < gameStateReference.board.height(); i++) {
                fillRow(i);
            }
        }
        scorePanel.removeAll();
        synchronized (gameStateReference.scoreBoard) {
            gameStateReference.scoreBoard.forEach((p, s) -> scorePanel.add(new JLabel(p + " : " + s)));
        }
        boardPanel.revalidate();
        scorePanel.revalidate();
        boardPanel.repaint();
        scorePanel.repaint();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void attachListener(JRadioButton endpoint) {
        endpoint.addActionListener(e -> {
            if (firstEndpointIsSelected) {
                controllerReference.input = controllerReference.input + " " + endpoint.getName();
                controllerReference.inputHasBeenReceivedSem.release();
            } else {
                controllerReference.input = endpoint.getName();
                firstEndpointIsSelected = true;
            }
        });
    }

    private void fillRow(final int row) {
        fillPointsRow(row);
        if(row < gameStateReference.board.height()){
            fillLinesRow(row);
        }
    }

    private void fillPointsRow(final int row){
        for (int i = 0; i < gameStateReference.board.width(); i++) {
            JRadioButton point = new JRadioButton("o");
            point.setName(i + "" + row);
            boardPanel.add(point);
            boardPanel.revalidate();
            attachListener(point);
            if(i < gameStateReference.board.width() - 1) {
                boardPanel.add(addHorizontalLineIfPresent(i, row));
                boardPanel.revalidate();
            }
        }
    }

    private void fillLinesRow(final int row) {
        for (int i = 0; i < gameStateReference.board.width(); i++) {
            boardPanel.add(addVerticalLineIfPresent(i, row));
            boardPanel.revalidate();
            if(i < gameStateReference.board.width() - 1) {
                boardPanel.add(new JLabel("spazio vuoto"));
                boardPanel.revalidate();
            }
        }
    }

    private JLabel addVerticalLineIfPresent(final int i, final int j) {
        if(gameStateReference.board.lineSitsBetween(new Point(i,j), new Point(i, j+1))){
            return new JLabel("|");
        }
        return new JLabel("spazio vuoto");
    }

    private JLabel addHorizontalLineIfPresent(final int i, final int j) {
        if(gameStateReference.board.lineSitsBetween(new Point(i,j), new Point(i+1, j))){
            return new JLabel("---");
        }
        return new JLabel("spazio vuoto");
    }
}

class LineSprite extends JLabel{

}
