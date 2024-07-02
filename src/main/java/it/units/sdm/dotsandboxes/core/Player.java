package it.units.sdm.dotsandboxes.core;

import java.util.Objects;
import java.util.UUID;

public record Player(String name, Color color){
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(name, ((Player) o).name);
    }

    @Override
    public String toString() {
        return name;
    }
}
