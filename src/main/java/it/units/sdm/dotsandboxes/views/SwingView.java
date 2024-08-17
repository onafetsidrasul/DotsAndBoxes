package it.units.sdm.dotsandboxes.views;

import it.units.sdm.dotsandboxes.controllers.IGameController;
import it.units.sdm.dotsandboxes.core.ColoredLine;
import it.units.sdm.dotsandboxes.core.Game;
import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Point;
import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

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
            return false;
        }
        return true;
    }

    @Override
    protected boolean finishConfigure() {
        try {
            boardPanel = new BoardPanel(gameStateReference, controllerReference);
            scorePanel = new ScorePanel(gameStateReference);

            mainPanel.add(boardPanel, BorderLayout.CENTER);
            mainPanel.add(scorePanel, BorderLayout.EAST);

            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (Exception e) {
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

                    // Slight delay to ensure the last line is drawn before closing the window
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
            scorePanel.updateTurn();
            scorePanel.updateScore();
        }
        isRefreshingUISem.release();
    }

    public static class BoardPanel extends JPanel {
        private static final int PADDING = 20;
        private static final int DOT_SIZE = 20;
        private static final int LINE_THICKNESS = 2;

        private final List<JRadioButton> selectedButtons = new ArrayList<>();
        private final Game gameStateReference;
        private final IGameController controllerReference;

        private Point firstPoint;
        private boolean isSelectingFirstPoint = true;

        public BoardPanel(Game gameStateReference, IGameController controllerReference) {
            this.gameStateReference = gameStateReference;
            this.controllerReference = controllerReference;
            setLayout(null);
            initializeDotButtons();
            addResizeListener();
        }

        private void initializeDotButtons() {
            int width = gameStateReference.board.width();
            int height = gameStateReference.board.height();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    add(createDotButton(x, y));
                }
            }
            updateDotButtonPositions();
        }

        private JRadioButton createDotButton(int x, int y) {
            JRadioButton dotButton = new JRadioButton();
            dotButton.setPreferredSize(new Dimension(DOT_SIZE, DOT_SIZE));
            dotButton.setActionCommand(x + " " + y);
            dotButton.addActionListener(new DotButtonActionListener());
            return dotButton;
        }

        private void addResizeListener() {
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    updateDotButtonPositions();
                    repaint();
                }
            });
        }

        private void updateDotButtonPositions() {
            int width = gameStateReference.board.width();
            int height = gameStateReference.board.height();

            int cellWidth = calculateCellDimension(getWidth(), width);
            int cellHeight = calculateCellDimension(getHeight(), height);

            for (Component component : getComponents()) {
                if (component instanceof JRadioButton) {
                    updateButtonPosition((JRadioButton) component, cellWidth, cellHeight);
                }
            }
        }

        private void updateButtonPosition(JRadioButton button, int cellWidth, int cellHeight) {
            String[] coordinates = button.getActionCommand().split(" ");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int dotX = PADDING + x * cellWidth;
            int dotY = PADDING + y * cellHeight;
            button.setBounds(dotX - DOT_SIZE / 2, dotY - DOT_SIZE / 2, DOT_SIZE, DOT_SIZE);
        }

        private int calculateCellDimension(int totalSize, int count) {
            return (totalSize - 2 * PADDING) / (count - 1);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = gameStateReference.board.width();
            int height = gameStateReference.board.height();

            int cellWidth = calculateCellDimension(getWidth(), width);
            int cellHeight = calculateCellDimension(getHeight(), height);

            synchronized (gameStateReference.board.lines()) {
                drawLines(g2, cellWidth, cellHeight, true);
                drawLines(g2, cellWidth, cellHeight, false);
            }
        }

        private void drawLines(Graphics2D g2, int cellWidth, int cellHeight, boolean isHorizontal) {
            int width = gameStateReference.board.width();
            int height = gameStateReference.board.height();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    drawLineIfPresent(g2, x, y, cellWidth, cellHeight, isHorizontal);
                }
            }
        }

        private void drawLineIfPresent(Graphics2D g2, int x, int y, int cellWidth, int cellHeight, boolean isHorizontal) {
            Line line = createLine(x, y, isHorizontal);

            Optional<ColoredLine> lineOpt = gameStateReference.board.lines().stream()
                    .filter(coloredLine -> coloredLine.hasSameEndpointsAs(line))
                    .findFirst();

            lineOpt.ifPresent(coloredLine -> drawLine(g2, coloredLine, x, y, cellWidth, cellHeight, isHorizontal));
        }

        private Line createLine(int x, int y, boolean isHorizontal) {
            try {
                return isHorizontal ? new Line(x, y, x + 1, y) : new Line(x, y, x, y + 1);
            } catch (InvalidInputException e) {
                throw new RuntimeException("Invalid line coordinates", e);
            }
        }

        private void drawLine(Graphics2D g2, ColoredLine coloredLine, int x, int y, int cellWidth, int cellHeight, boolean isHorizontal) {
            g2.setColor(coloredLine.color().toAwtColor());
            int dotX = PADDING + x * cellWidth;
            int dotY = PADDING + y * cellHeight;

            if (isHorizontal) {
                g2.fillRect(dotX, dotY - LINE_THICKNESS / 2, cellWidth, LINE_THICKNESS);
            } else {
                g2.fillRect(dotX - LINE_THICKNESS / 2, dotY, LINE_THICKNESS, cellHeight);
            }
        }

        private class DotButtonActionListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point clickedPoint = parseCoordinates(e.getActionCommand());
                handleDotSelection(clickedPoint, (JRadioButton) e.getSource());
            }

            private Point parseCoordinates(String actionCommand) {
                String[] coords = actionCommand.split(" ");
                return new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            }

            private void handleDotSelection(Point clickedPoint, JRadioButton sourceButton) {
                selectedButtons.add(sourceButton);

                if (isSelectingFirstPoint) {
                    firstPoint = clickedPoint;
                    isSelectingFirstPoint = false;
                } else {
                    if (firstPoint != null) {
                        processLineSelection(clickedPoint);
                        resetSelection();
                    }
                }
            }

            private void processLineSelection(Point secondPoint) {
                controllerReference.writeInput(formatLineInput(firstPoint, secondPoint));
                controllerReference.inputHasBeenReceivedSem.release();
            }

            private String formatLineInput(Point first, Point second) {
                return first.x() + " " + first.y() + " " + second.x() + " " + second.y();
            }

            private void resetSelection() {
                firstPoint = null;
                isSelectingFirstPoint = true;

                if (selectedButtons.size() == 2) {
                    selectedButtons.forEach(button -> button.setSelected(false));
                    selectedButtons.clear();
                }
            }
        }
    }

    private static class ScorePanel extends JPanel {
        private final JLabel scoreLabels;
        private final JLabel turnLabel;
        private final Game gameStateReference;

        public ScorePanel(Game gameStateReference) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            scoreLabels = new JLabel();
            turnLabel = new JLabel("Turn: Player 1");
            add(scoreLabels);
            add(turnLabel);
            this.gameStateReference = gameStateReference;
        }

        public void updateScore() {
            StringBuilder scores = new StringBuilder("<html>");
            for (int i = 1; i <= gameStateReference.players.size(); i++) {
                String player = gameStateReference.players.get(i - 1);
                String score = String.valueOf(gameStateReference.scoreBoard.get(player));
                scores.append(player).append(": ").append(score).append("<br>");
            }
            scores.append("</html>");
            scoreLabels.setText(scores.toString());
        }

        public void updateTurn() {
            String currentPlayer = gameStateReference.currentPlayer();
            turnLabel.setText("Current player: " + currentPlayer);
            Color playerColor = gameStateReference.playerColorLUT.get(currentPlayer).toAwtColor();
            turnLabel.setForeground(playerColor);
        }
    }
}
