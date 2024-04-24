package it.units.sdm.dotsandboxes.core;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;

public enum Color {
    RED(new AnsiFormat(Attribute.RED_TEXT())),
    BLUE(new AnsiFormat(Attribute.BLUE_TEXT())),
    GREEN(new AnsiFormat(Attribute.GREEN_TEXT())),
    YELLOW(new AnsiFormat(Attribute.YELLOW_TEXT())),
    CYAN(new AnsiFormat(Attribute.CYAN_TEXT())),
    MAGENTA(new AnsiFormat(Attribute.MAGENTA_TEXT()));

    private final AnsiFormat format;

    Color(AnsiFormat format) {
        this.format = format;
    }

    public AnsiFormat getFormat() {
        return format;
    }
}