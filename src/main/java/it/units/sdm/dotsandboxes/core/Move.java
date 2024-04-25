package it.units.sdm.dotsandboxes.core;

public final class Move {
    private final Player player;
    private final Integer colorlessNormalizedLineHash;

    public Move(Player player, Line line) {
        this.player = player;
        colorlessNormalizedLineHash = new Line(null, Line.normalize(line)).hashCode();
    }

    public Player player() {
        return player;
    }

    public Integer colorlessNormalizedLineHash() {
        return colorlessNormalizedLineHash;
    }

    @Override
    public String toString() {
        return "Move[" +
                "player=" + player + ", " +
                "lineHash=" + colorlessNormalizedLineHash + ']';
    }

}
