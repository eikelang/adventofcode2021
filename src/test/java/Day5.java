import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day5 {

    private Stream<String> readFile() {
        var inputStream = Day5.class.getResourceAsStream("/day5.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    @Test
    void countIntersections() {
        final var pointsTouchedByLines = new HashMap<Coordinate, Integer>();
        readFile().map(LineParser::new)
                .flatMap(LineParser::fullLine)
                .forEach(c -> pointsTouchedByLines.merge(c, 1, Integer::sum));

        assertThat(pointsTouchedByLines.values().stream().filter(v -> v > 1).count()).isEqualTo(4745L);
    }

    @Test
    void countIntersectionsWithDiagonals() {
        final var pointsTouchedByLines = new HashMap<Coordinate, Integer>();
        readFile().map(LineParser::new)
                .flatMap(LineParser::fullLineSupportingDiagonals)
                .forEach(c -> pointsTouchedByLines.merge(c, 1, Integer::sum));

        assertThat(pointsTouchedByLines.values().stream().filter(v -> v > 1).count()).isEqualTo(18442L);
    }
}
