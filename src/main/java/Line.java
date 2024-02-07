public record Line(Color color, int x1, int y1, int x2, int y2) {
    public Line(int x1, int y1, int x2, int y2) {
        this(null, x1, y1, x2, y2);
    }

}
