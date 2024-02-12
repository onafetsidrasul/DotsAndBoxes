package it.units.sdm.dotsandboxes.core;

public record Line(Color color, int x1, int y1, int x2, int y2) {
    public Line(int x1, int y1, int x2, int y2) {
        this(null, x1, y1, x2, y2);
    }

    public Line(Color color, Line line) {
        // overwrites the color of the supplied line
        this(color, line.x1(), line.y1(), line.x2(), line.y2());
    }
}
