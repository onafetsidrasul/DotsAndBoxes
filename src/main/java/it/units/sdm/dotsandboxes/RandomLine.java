package it.units.sdm.dotsandboxes;

import it.units.sdm.dotsandboxes.core.Line;
import it.units.sdm.dotsandboxes.core.Point;

public class RandomLine {
    public static Line randomCandidate(int[] dims) {
        Line candidate;
        Point p1 = getFirstPoint(dims);
        Point p2;
        do {p2 = getSecondPoint(p1);
        } while (!isValidPoint(p2.x(), p2.y(), dims));
        candidate = new Line(p1.x(), p1.y(), p2.x(), p2.y());
        return candidate;
    }

    private static Point getFirstPoint(int[] dims) {
        int x1 = (int) Math.floor(Math.random() * dims[0]);
        int y1 = (int) Math.floor(Math.random() * dims[1]);
        return new Point(x1, y1);
    }

    private static Point getSecondPoint(Point p){
        if (Math.random()>=0.5){
            return new Point(p.x(),p.y()+(Math.random() >= 0.5 ? 1 : -1));
        }else{
            return new Point(p.x()+(Math.random() >= 0.5 ? 1 : -1),p.y());
        }
    }

    private static boolean isValidPoint(int x, int y, int[] dims) {
        return x >= 0 && x < dims[0] && y >= 0 && y < dims[1];
    }
}
