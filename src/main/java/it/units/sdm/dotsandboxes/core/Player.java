package it.units.sdm.dotsandboxes.core;

import java.util.UUID;

public class Player {

    private final String id;
    private final String name;
    private final Color color;

    public Player(String name, Color color) {
        this(UUID.randomUUID().toString(), name, color);
    }

    public Player(String id, String name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public String name() {
        return name;
    }

    public Color color() {
        return color;
    }

    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", color=" + color +
                '}';
    }
}
