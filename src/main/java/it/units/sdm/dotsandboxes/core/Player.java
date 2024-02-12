package it.units.sdm.dotsandboxes.core;

public class Player {
    private final String name;
    private final Color color;
    private int score;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.score = 0;
    }

    public int getScore() {
        return score;
    }

    public void increaseScore() {
        score++;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
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
