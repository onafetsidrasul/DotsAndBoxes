import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Tests {
    @Test
    void DoNotAllowLinesLongerThan1() { assertThrows(Exception.class, () -> new Line(0, 0, 0, 2));
    }

    @Test
    void DoNotAllowDiagonalLines() { assertThrows(Exception.class, () -> new Line(0, 0, 1, 1));
    }
}