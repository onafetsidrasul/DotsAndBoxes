package it.units.sdm.dotsandboxes.core;

import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;

import java.util.*;

public class Board {
    private final SequencedCollection<ColoredLine> lines;
    private final int width, height;

    public Board(int width, int height) {
        if (width < 2 || height < 2) {
            throw new IllegalArgumentException("Board width and height must be greater than 2");
        }
        this.width = width;
        this.height = height;
        lines = new ArrayList<>();
    }

    /**
     * @param p The upper left point of the box to check
     * @return false if the box is not completed, true otherwise.
     */
    public boolean isBoxCompleted(Point p) {
        if (p.x() < 0 || p.x() >= width || p.y() < 0 || p.y() >= height) {
            throw new IllegalArgumentException("Point does not represent a square on the board");
        }
        return lineSitsBetween(p, new Point(p.x(), p.y() + 1)) &&
                lineSitsBetween(p, new Point(p.x() + 1, p.y())) &&
                lineSitsBetween(new Point(p.x() + 1, p.y() + 1), new Point(p.x(), p.y() + 1)) &&
                lineSitsBetween(new Point(p.x() + 1, p.y() + 1), new Point(p.x() + 1, p.y()));
    }

    public boolean isBoardFull() {
        // since we check the validity of every line drawn onto the board we can check if the board has been completely filled
        // (i.e. the games has ended) by simply checking if the number of lines is 2*n*m - n - m which is the amount of possible lines
        // for a n*m board
        return lines.size() == (2 * width * height) - width - height;
    }

    protected void placeLine(ColoredLine line) throws InvalidInputException {
        if (line.length() != 1)
            throw new InvalidInputException("Line is too long.");
        if (isLineOutOfBounds(line))
            throw new InvalidInputException("Line sits outside the bounds of the board.");
        if (lines.parallelStream().anyMatch(l -> l.hasSameEndpointsAs(line))) {
            throw new InvalidInputException("A line already exists between endpoints " + line.p1() + " and " + line.p2());
        }
        lines.add(line);
    }

    private boolean isLineOutOfBounds(Line line) {
        return line.p1().x() < 0 || line.p1().x() >= width ||
                line.p1().y() < 0 || line.p1().y() >= height ||
                line.p2().x() < 0 || line.p2().x() >= width ||
                line.p2().y() < 0 || line.p2().y() >= height;
    }

    private boolean lineSitsBetween(Point p1, Point p2) {
        return lines.parallelStream().anyMatch(l -> {
            try {
                return l.hasSameEndpointsAs(new Line(p1, p2));
            } catch (InvalidInputException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public int length() {
        return width;
    }

    public int height() {
        return height;
    }

    public SequencedCollection<ColoredLine> lines() {
        return lines;
    }
}
