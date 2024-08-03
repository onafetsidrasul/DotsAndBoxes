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
import java.util.ArrayList;
import java.util.Optional;

public class BoardPanel extends JPanel {
    private final java.util.List<JRadioButton> selectedButtons = new ArrayList<>();
    private it.units.sdm.dotsandboxes.core.Point firstPoint;
    private boolean isSelectingFirstPoint = true;
    private static final int PADDING = 20;

    private final Game gameStateReference;
    private final IGameController controllerReference;

    public BoardPanel(Game gameStateReference, IGameController controllerReference) {
        this.gameStateReference = gameStateReference;
        this.controllerReference = controllerReference;
        setLayout(null);

        int width = gameStateReference.board.width();
        int height = gameStateReference.board.height();

        initializeDotButtons(width, height);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateDotButtonPositions();
                repaint();
            }
        });
    }

    private void initializeDotButtons(int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                JRadioButton dotButton = createDotButton(x, y);
                add(dotButton);
            }
        }
        updateDotButtonPositions();
    }

    private void updateDotButtonPositions() {
        int width = gameStateReference.board.width();
        int height = gameStateReference.board.height();

        int cellWidth = calculateCellDimension(getWidth(), width);
        int cellHeight = calculateCellDimension(getHeight(), height);

        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JRadioButton) {
                String[] coordinates = ((JRadioButton) component).getActionCommand().split(" ");
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);
                int dotX = PADDING + x * cellWidth;
                int dotY = PADDING + y * cellHeight;
                component.setBounds(dotX - 10, dotY - 10, 20, 20);
            }
        }
    }

    private int calculateCellDimension(int totalSize, int count) {
        return (totalSize - 2 * PADDING) / (count - 1);
    }

    private JRadioButton createDotButton(int x, int y) {
        JRadioButton dotButton = new JRadioButton();
        dotButton.setPreferredSize(new Dimension(20, 20));
        dotButton.setActionCommand(x + " " + y);
        dotButton.addActionListener(new DotButtonActionListener());
        return dotButton;
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
            drawLines(g2, width, height, cellWidth, cellHeight, true);
            drawLines(g2, width, height, cellWidth, cellHeight, false);
        }
    }

    private void drawLines(Graphics2D g2, int width, int height, int cellWidth, int cellHeight, boolean isHorizontal) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int dotX = BoardPanel.PADDING + x * cellWidth;
                int dotY = BoardPanel.PADDING + y * cellHeight;

                Line line;
                try {
                    line = isHorizontal ? new Line(x, y, x + 1, y) : new Line(x, y, x, y + 1);
                } catch (InvalidInputException e) {
                    throw new RuntimeException(e);
                }
                Optional<ColoredLine> lineOpt = gameStateReference.board.lines().stream()
                        .filter(coloredLine -> coloredLine.hasSameEndpointsAs(line))
                        .findFirst();
                if (lineOpt.isPresent()) {
                    g2.setColor(lineOpt.get().color().toAwtColor());
                    if (isHorizontal) {
                        g2.fillRect(dotX, dotY - 1, cellWidth, 2);
                    } else {
                        g2.fillRect(dotX - 1, dotY, 2, cellHeight);
                    }
                }
            }
        }
    }

    private class DotButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] coords = e.getActionCommand().split(" ");
            it.units.sdm.dotsandboxes.core.Point clickedPoint;
            clickedPoint = new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
            JRadioButton sourceButton = (JRadioButton) e.getSource();
            selectedButtons.add(sourceButton);

            if (isSelectingFirstPoint) {
                firstPoint = clickedPoint;
                isSelectingFirstPoint = false;
            } else {
                if (firstPoint != null) {
                    sourceButton.setSelected(false);
                    controllerReference.input = firstPoint.x() + " " + firstPoint.y() + " " + clickedPoint.x() + " " + clickedPoint.y();
                    controllerReference.inputHasBeenReceivedSem.release();
                    firstPoint = null;
                    sourceButton.setSelected(false);
                }
                isSelectingFirstPoint = true;
                if (selectedButtons.size() == 2) {
                    for (JRadioButton button : selectedButtons) {
                        button.setSelected(false);
                    }
                    selectedButtons.clear();
                }
            }
        }
    }
}
