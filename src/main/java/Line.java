public record Line(int x1, int y1, int x2, int y2) {
    public Line{
        if (Math.pow((x2-x1),2)+Math.pow((y2-y1),2)!=1) {
            throw new IllegalArgumentException("Invalid line");
        }
    }
}
