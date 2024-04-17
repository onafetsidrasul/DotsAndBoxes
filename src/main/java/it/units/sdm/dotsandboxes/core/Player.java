package it.units.sdm.dotsandboxes.core;

public record Player(String name, Color color) {
    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", color=" + color +
                '}';
    }
}
