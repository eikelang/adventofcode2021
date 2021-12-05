import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinateParserTest {

    @Test
    void ignoresNonOrthogonalLine() {
        final LineParser lineParser = new LineParser("200,785 -> 884,101");
        assertThat(lineParser.fullLine()).isEmpty();
    }

    @Test
    void parsesHorizontalLine() {
        final LineParser lineParser = new LineParser("3, 0 -> 3,5");
        assertThat(lineParser.fullLine()).containsExactlyInAnyOrder(
                Coordinate.of(3, 0),
                Coordinate.of(3, 1),
                Coordinate.of(3, 2),
                Coordinate.of(3, 3),
                Coordinate.of(3, 4),
                Coordinate.of(3, 5));
    }

    @Test
    void parsesVerticalLines() {
        final LineParser lineParser = new LineParser("2, 5 -> 2,1");
        assertThat(lineParser.fullLine()).containsExactlyInAnyOrder(
                Coordinate.of(2, 5),
                Coordinate.of(2, 4),
                Coordinate.of(2, 3),
                Coordinate.of(2, 2),
                Coordinate.of(2, 1));
    }

    @Test
    void parsesDiagonalLineEasyDirection() {
        final LineParser lineParser = new LineParser("1, 1 -> 3,3");
        assertThat(lineParser.fullLineSupportingDiagonals()).containsExactlyInAnyOrder(
                Coordinate.of(1, 1),
                Coordinate.of(2, 2),
                Coordinate.of(3, 3));

    }

    @Test
    void parsesDiagonalLineEasyDirectionReversed() {
        final LineParser lineParser = new LineParser("3, 3 -> 1,1");
        assertThat(lineParser.fullLineSupportingDiagonals()).containsExactlyInAnyOrder(
                Coordinate.of(1, 1),
                Coordinate.of(2, 2),
                Coordinate.of(3, 3));

    }

    @Test
    void parsesDiagonalLineHarderDirection() {
        final LineParser lineParser = new LineParser("3, 5 -> 5,3");
        assertThat(lineParser.fullLineSupportingDiagonals()).containsExactlyInAnyOrder(
                Coordinate.of(3, 5),
                Coordinate.of(4, 4),
                Coordinate.of(5, 3));

    }

    @Test
    void parsesDiagonalLineHarderDirectionReversed() {
        final LineParser lineParser = new LineParser("5, 3 -> 3,5");
        assertThat(lineParser.fullLineSupportingDiagonals()).containsExactlyInAnyOrder(
                Coordinate.of(3, 5),
                Coordinate.of(4, 4),
                Coordinate.of(5, 3));

    }
}
