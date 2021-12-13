package day13;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day13 {

    private Stream<String> readFile() {
        var inputStream = Day13.class.getResourceAsStream("/day13.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    private Stream<String> readExample(final String example) {
        return example.lines();
    }

    @Test
    void firstFold() {
        final var grid = readFile().takeWhile(s -> s.contains(","))
                .map(s -> s.split(","))
                .map(a -> new Coordinate(a[0], a[1]))
                .collect(Collectors.toSet());
        final var afterFirstFold = grid.stream()
                .map(coord -> coord.foldX(655))
                .collect(Collectors.toSet());
        assertThat(afterFirstFold).hasSize(837);
    }

    @Test
    void testFoldXSimpleLastBecomesFirst() {
        final Coordinate input = new Coordinate(2,0);
        assertThat(input.foldX(1)).isEqualTo(new Coordinate(0,0));
    }

    @Test
    void testFoldXSimpleFirstAfterFoldBecomesLastBeforeFold() {
        final Coordinate input = new Coordinate(5,0);
        assertThat(input.foldX(4)).isEqualTo(new Coordinate(3,0));
    }

    class Coordinate {
        private int x;
        private int y;

        public Coordinate(final String xCoord, final String yCoord) {
            x = Integer.parseInt(xCoord);
            y = Integer.parseInt(yCoord);
        }

        public Coordinate(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Coordinate that = (Coordinate) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        private Coordinate foldX(final int foldLine) {
            if (x < foldLine) {
                return this;
            } else {
                return new Coordinate(foldLine - (x - foldLine), this.y);
            }
        }

        @Override
        public String toString() {
            return "Coordinate{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
