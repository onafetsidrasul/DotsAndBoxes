package it.units.sdm.dotsandboxes.core;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;

public enum Color {
    RED(new AnsiFormat(Attribute.RED_TEXT())),
    BLUE(new AnsiFormat(Attribute.BLUE_TEXT()));

    private final AnsiFormat format;

    Color(AnsiFormat format) {
        this.format = format;
    }

    public AnsiFormat getFormat() {
        return format;
    }
}