package it.units.sdm.dotsandboxes.core;

public final class Player {
    private final String name;
    private final Color color;
    private int score;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.score = 0;
    }

    public int score() {
        return score;
    }

    public void increaseScoreByOne() {
        score++;
    }

    public String name() {
        return name;
    }

    public Color color() {
        return color;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", color=" + color +
                ", score=" + score +
                '}';
    }
}
