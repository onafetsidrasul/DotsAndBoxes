package it.units.sdm.dotsandboxes.core;

import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;

/**
 * Class that models a colored line on a Dots And Boxes board. *
 */
public class ColoredLine extends Line {
    private final Color color;

    public ColoredLine(Line l, Color color) throws InvalidInputException {
        super(l.p1(), l.p2());
        this.color = color;
    }

    public Color color() {
        return color;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && color.equals(((ColoredLine) obj).color);
    }

    @Override
    public String toString() {
        return color + ", " + super.toString();
    }

    public boolean hasSameEndpointsAs(Line other) {
        try {
            return (new Line(p1(), p2())).equals(new Line(other.p1(), other.p2()));
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
    }
}
