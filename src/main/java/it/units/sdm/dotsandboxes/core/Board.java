package it.units.sdm.dotsandboxes.core;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private final Map<Integer, Line> lines;
    private final int length, height;

    public Board(int xDimension, int yDimension) {
        length = xDimension;
        height = yDimension;
        lines = new HashMap<>();
    }

    public boolean isBoxCompleted(Point p) {
        //box identified by the upper left point
        Integer upperSideHash = new Line(p.x(), p.y(), p.x() + 1, p.y()).hashCode();
        Integer lowerSideHash = new Line(p.x(), p.y() + 1, p.x() + 1, p.y() + 1).hashCode();
        Integer leftSideHash = new Line(p.x(), p.y(), p.x(), p.y() + 1).hashCode();
        Integer rightSideHash = new Line(p.x() + 1, p.y(), p.x() + 1, p.y() + 1).hashCode();

        return lines.containsKey(upperSideHash) &&
                lines.containsKey(lowerSideHash) &&
                lines.containsKey(leftSideHash) &&
                lines.containsKey(rightSideHash);
    }

    public boolean isBoardFull() {
        // since we check the validity of every line drawn onto the board we can check if the board has been completely filled
        // (i.e. the games has ended) by simply checking if the number of lines is 2*n*m - n - m which is the amount of possible lines
        // for a n*m board
        return lines.size() == (2 * length * height) - length - height;
    }

    protected void addLine(Line line) {
        if (line.length() != 1)
            throw new IllegalArgumentException("Line is too long!");
        if (isLineOutOfBounds(line))
            throw new IllegalArgumentException("Line sits outside the bounds of the board!");
        if (lines.containsKey(new Line(null, Line.normalize(line)).hashCode())) {
            throw new IllegalArgumentException("Line already exists!");
        }
        // the hashcode is calculated based on the line stripped of its color and normalized in order to avoid putting to lines of different colors in the same place
        lines.put(new Line(null, Line.normalize(line)).hashCode(), line);
    }

    private boolean isLineOutOfBounds(Line line) {
        return line.p1().x() < 0 || line.p1().x() >= length || line.p1().y() < 0 || line.p1().y() >= height ||
                line.p2().x() < 0 || line.p2().x() >= length || line.p2().y() < 0 || line.p2().y() >= height;
    }

    public int length() {
        return length;
    }

    public int height() {
        return height;
    }

    public Map<Integer, Line> getLines() {
        return lines;
    }
}
