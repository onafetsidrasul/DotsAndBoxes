package it.units.sdm.dotsandboxes.core;

import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;

import java.util.*;

/**
 * Class modeling a Dots and Boxes board.
 */
public class Board {
    private final SequencedCollection<ColoredLine> lines;
    private final int height, width;

    public Board(int height, int width) {
        if (width < 2 || height < 2) {
            throw new IllegalArgumentException("Board width and height must be greater or equal than 2");
        }
        this.height = height;
        this.width = width;
        lines = Collections.synchronizedList(new ArrayList<>());
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

    /**
     * @return true, if the board is full. False, otherwise.
     */
    public boolean isBoardFull() {
        /* since we check the validity of every line drawn onto the board we can check if the board has been completely filled
         * (i.e. the games has ended) by simply checking if the number of lines is 2*n*m - n - m which is the amount of possible lines
         * for a n*m board
         */
        return lines.size() == (2 * width * height) - width - height;
    }

    /** Try placing a line onto the board.
     * @param line the line to be placed.
     * @throws InvalidInputException if the line is not valid.
     */
    protected void placeLine(ColoredLine line) throws InvalidInputException {
        if(line == null){
            throw new InvalidInputException("Line is invalid.");
        }
        if (line.length() != 1)
            throw new InvalidInputException("Line is too long.");
        if (isLineOutOfBounds(line))
            throw new InvalidInputException("Line sits outside the bounds of the board.");
        if (lines.parallelStream().anyMatch(l -> l.hasSameEndpointsAs(line))) {
            throw new InvalidInputException("A line already exists between endpoints " + line.p1() + " and " + line.p2());
        }
        synchronized (lines){
            lines.add(line);
        }
    }

    private boolean isLineOutOfBounds(Line line) {
        return line.p1().x() < 0 || line.p1().x() >= width ||
                line.p1().y() < 0 || line.p1().y() >= height ||
                line.p2().x() < 0 || line.p2().x() >= width ||
                line.p2().y() < 0 || line.p2().y() >= height;
    }

    /**
     * @param p1 the "first" endpoint.
     * @param p2 the "second" endpoint.
     * @return if a line occupies the two endpoints.
     */
    public boolean lineSitsBetween(Point p1, Point p2) {
        synchronized (lines){
            return lines.parallelStream().anyMatch(l -> {
                try {
                    return l.hasSameEndpointsAs(new Line(p1, p2));
                } catch (InvalidInputException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public SequencedCollection<ColoredLine> lines() {
        return lines;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != this.getClass()){
            return false;
        }
        Board other = (Board) obj;
        return width == other.width && height == other.height && lines.equals(other.lines);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(ColoredLine l : lines){
            s.append(l.toString()).append("\n");
        }
        return s.toString();
    }
}
