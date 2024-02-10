public record Line(Color color, Point p1, Point p2) {
    public Line(int x1, int y1, int x2, int y2) {
        this(null, new Point(x1, y1), new Point(x2, y2));
    }

    public int x1(){return p1.x();}
    public int y1(){return p1.y();}
    public int x2(){return p2.x();}
    public int y2(){return p2.y();}

    public Line(Color color, Line line) {
        // overwrites the color of the supplied line
        this(color, line.p1(), line.p2());
    }
}
