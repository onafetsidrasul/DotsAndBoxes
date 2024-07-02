package it.units.sdm.dotsandboxes.core;

import it.units.sdm.dotsandboxes.exceptions.InvalidInputException;

import java.util.LinkedHashSet;
import java.util.SequencedSet;

/**
 * Class that models a  line.
 * Lines do not formally have a direction as it's not required by the game,
 * although it can be inferred from the implicit order of the endpoints.
 */
public class Line {

    private final SequencedSet<Point> endpoints;

    public Line(Point p1, Point p2) throws InvalidInputException {
        endpoints = new LinkedHashSet<>();
        endpoints.add(p1);
        endpoints.add(p2);
        if(endpoints.size() != 2) {
            throw new InvalidInputException("A line must sit between two different points.");
        }
    }

    public Line(int x1, int y1, int x2, int y2) throws InvalidInputException {
        this(new Point(x1, y1), new Point(x2, y2));
    }

    public Point p1(){
        return endpoints.getFirst();
    }

    public Point p2(){
        return endpoints.getLast();
    }

    public double length() {
        return Math.pow(Math.pow(p2().x() - p1().x(), 2) + Math.pow(p2().y() - p1().y(), 2), 0.5);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Line other) {
            return endpoints.equals(other.endpoints);
        }
        return false;
    }

    @Override
    public String toString() {
        return p1() + " -> " + p2();
    }
}

