package day13;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
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
    void allFolds() {
        final var grid = readFile().takeWhile(s -> s.contains(","))
                .map(s -> s.split(","))
                .map(a -> new Coordinate(a[0], a[1]))
                .collect(Collectors.toSet());

        final Function<Coordinate, Coordinate> foldAll = readFile().dropWhile(s -> !s.isEmpty())
                .map(this::readInstruction)
                .reduce(Function.identity(), Function::andThen);

        final Set<Coordinate> allFolded = grid.stream().map(foldAll).collect(Collectors.toSet());

        int maxX = allFolded.stream().mapToInt(coord -> coord.x).max().orElse(0);
        int maxY = allFolded.stream().mapToInt(coord -> coord.y).max().orElse(0);

        for (int y = 0; y<=maxY; y++) {
            for (int x = 0; x<=maxX; x++) {
                System.out.print(allFolded.contains(new Coordinate(x,y)) ? "*" : " ");
            }
            System.out.println();
        }
        assertThat(allFolded).hasSize(99);
    }

    @Test
    void testFoldXSimpleLastBecomesFirst() {
        final Coordinate input = new Coordinate(2, 0);
        assertThat(input.foldX(1)).isEqualTo(new Coordinate(0, 0));
    }

    @Test
    void testFoldXSimpleFirstAfterFoldBecomesLastBeforeFold() {
        final Coordinate input = new Coordinate(5, 0);
        assertThat(input.foldX(4)).isEqualTo(new Coordinate(3, 0));
    }

    private Function<Coordinate,Coordinate> readInstruction(final String instruction) {
        if (!instruction.startsWith("fold along")) {
            return Function.identity();
        }
        final var actualCommand = instruction.substring(instruction.lastIndexOf(" ")).trim();
        final var split = actualCommand.split("=");
        final var axis = split[0];
        final var foldPoint = Integer.parseInt(split[1]);
        if ("x".equals(axis)) {
            return coord -> coord.foldX(foldPoint);
        } else {
            return coord -> coord.foldY(foldPoint);
        }
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

        private Coordinate foldY(final int foldLine) {
            if (y < foldLine) {
                return this;
            } else {
                return new Coordinate(x, foldLine - (y - foldLine));
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
