public record Line(Player player, int x1, int y1, int x2, int y2) {
    public Line {
        if (Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2) != 1) {
            throw new IllegalArgumentException("Illegal line");
        }
    }

    public Line(int x1, int y1, int x2, int y2) {
        this(null, x1, y1, x2, y2);
    }

    public boolean equalsIgnoringPlayer(Line other) {
        return (this.x1 == other.x1 && this.y1 == other.y1 && this.x2 == other.x2 && this.y2 == other.y2) ||
                (this.x1 == other.x2 && this.y1 == other.y2 && this.x2 == other.x1 && this.y2 == other.y1);
    }
}
