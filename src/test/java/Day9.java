import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day9 {

    private static final String SMALL_EXAMPLE = """
            2199943210
            3987894921
            9856789892
            8767896789
            9899965678""";

    private Integer[][] readFile() {
        var inputStream = Day9.class.getResourceAsStream("/day9.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        final Stream<Integer[]> stream = buf.lines().map(line -> toArray(line.chars()));
        final List<Integer[]> collect = stream.collect(Collectors.toList());
        return collect.toArray(new Integer[collect.size()][]);
    }

    @Test
    void countLowPointsSample() {
        final Stream<Integer[]> stream = SMALL_EXAMPLE.lines().map(line -> toArray(line.chars()));
        final List<Integer[]> collect = stream.collect(Collectors.toList());
        final Integer[][] map = collect.toArray(new Integer[collect.size()][]);
        printMap(map);

        int lowPointSum = 0;
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (isLowPoint(x, y, map)) {
                    lowPointSum += 1 + map[y][x];
                }
            }
        }
        assertThat(lowPointSum).isEqualTo(15);
    }

    @Test
    void countLowPointsFull() {
        final Integer[][] map = readFile();

        final List<Coordinate> lowPoints = findLowPointsInMap(map);

        int riskLevelSum = lowPoints.stream().mapToInt(c -> map[c.y][c.x] + 1).sum();
        assertThat(riskLevelSum).isEqualTo(526);
    }

    @Test
    void findBasinsFull() {
        final Integer[][] map = readFile();

        printBasins(map);
    }

    private List<Coordinate> findLowPointsInMap(final Integer[][] map) {
        final List<Coordinate> lowPoints = new ArrayList<>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (isLowPoint(x, y, map)) {
                    lowPoints.add(new Coordinate(x,y));
                }
            }
        }
        return lowPoints;
    }

    @Test
    void testToArray() {
        final Integer[] integers = toArray("3245".chars());
        assertThat(integers).contains(3, 2, 4, 5);
    }

    private void printMap(Integer[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                System.out.print(map[y][x]);
            }
            System.out.println();
        }
    }

    private void printBasins(Integer[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                final Integer mapValue = map[y][x];
                final boolean isLowPoint = isLowPoint(x, y, map);
                if (isLowPoint) {
                    System.out.print('X');
                } else if (mapValue == 9) {
                    System.out.print('*');
                } else {
                    System.out.print(' ');
                }
            }
            System.out.println();
        }
    }

    private Integer[] toArray(IntStream input) {
        final List<Integer> collect = input.map(i -> i - '0')
                .boxed()
                .collect(Collectors.toList());
        return collect.toArray(new Integer[collect.size()]);
    }

    private boolean isLowPoint(int x, int y, Integer[][] map) {
        int mapValue = map[y][x];
        
        List<Integer> xValues =
                IntStream.rangeClosed(x - 1, x + 1)
                        .filter(i -> i >= 0 && i < map[0].length)
                        .filter(i -> i != x).boxed().collect(Collectors.toList());
        List<Integer> yValues =
                IntStream.rangeClosed(y - 1, y + 1)
                        .filter(i -> i >= 0 && i < map.length)
                        .filter(i -> i != y)
                        .boxed()
                        .collect(Collectors.toList());

        boolean isMin = true;
        for (int yi : yValues) {
            isMin = isMin && mapValue < map[yi][x];
        }
        for (int xi : xValues) {
            isMin = isMin && (mapValue < map[y][xi]);
        }

        return isMin;
    }

    private class Coordinate {
        int x;
        int y;

        public Coordinate(final int x, final int y) {

            this.x = x;
            this.y = y;
        }

        Set<Coordinate> neighboursInMap(final Integer[][] map) {
            final List<Integer> xValues =
                    IntStream.rangeClosed(x - 1, x + 1)
                            .filter(i -> i >= 0 && i < map[0].length)
                            .filter(i -> i != x).boxed().collect(Collectors.toList());
            final List<Integer> yValues =
                    IntStream.rangeClosed(y - 1, y + 1)
                            .filter(i -> i >= 0 && i < map.length)
                            .filter(i -> i != y)
                            .boxed()
                            .collect(Collectors.toList());
            final Set<Coordinate> neighbourhood = new HashSet<>();
            for (int yi : yValues) {
                if (map[yi][x] < 9) {
                    neighbourhood.add(new Coordinate(x, yi));
                }
            }
            for (int xi : xValues) {
                if (map[y][xi] < 9) {
                    neighbourhood.add(new Coordinate(xi, y));
                }
            }
            return neighbourhood;
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
    }
}
