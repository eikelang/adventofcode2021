import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

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
        final Integer[][] map = readString(SMALL_EXAMPLE);
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

    private Integer[][] readString(final String input) {
        final Stream<Integer[]> stream = input.lines().map(line -> toArray(line.chars()));
        final List<Integer[]> collect = stream.collect(Collectors.toList());
        final Integer[][] map = collect.toArray(new Integer[collect.size()][]);
        return map;
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

        final Integer result = findLowPointsInMap(map).stream().map(c -> findBasinAround(c.x, c.y, map)).map(Set::size)
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .reduce(1, (i1, i2) -> i1 * i2);

        assertThat(result).isEqualTo(1123524);
    }

    private List<Coordinate> findLowPointsInMap(final Integer[][] map) {
        final List<Coordinate> lowPoints = new ArrayList<>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (isLowPoint(x, y, map)) {
                    lowPoints.add(new Coordinate(x, y));
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

    @Test
    void testNeighbourCalculationNoNeighbours() {
        final Integer[][] map = readString("1");

        assertThat(new Coordinate(0, 0).neighboursInMap(map)).isEmpty();
    }

    @Test
    void testNeighbourCalculationSingleHorizontalNeighbour() {
        final Integer[][] map = readString("12");

        assertThat(new Coordinate(0, 0).neighboursInMap(map)).extracting(c -> c.x, c -> c.y)
                .containsExactly(tuple(1, 0));
    }

    @Test
    void testNeighbourCalculationSingleVerticalNeighbour() {
        final Integer[][] map = readString("""
                1
                2""");

        assertThat(new Coordinate(0, 0).neighboursInMap(map)).extracting(c -> c.x, c -> c.y)
                .containsExactly(tuple(0, 1));
    }

    @Test
    void testNeighbourCalculationCorner() {
        final Integer[][] map = readString("""
                12
                24""");

        assertThat(new Coordinate(0, 0).neighboursInMap(map)).extracting(c -> c.x, c -> c.y)
                .containsExactlyInAnyOrder(tuple(0, 1), tuple(1, 0));
    }

    @Test
    void testMiddleCel() {
        final Integer[][] map = readString(SMALL_EXAMPLE);

        assertThat(new Coordinate(2, 2).neighboursInMap(map)).extracting(c -> c.x, c -> c.y)
                .containsExactlyInAnyOrder(tuple(1, 2), tuple(2, 1), tuple(3, 2), tuple(2, 3));
    }

    @Test
    void findTopLeftBasin() {
        final Integer[][] map = readString(SMALL_EXAMPLE);

        final Set<Coordinate> basin = findBasinAround(0, 0, map);

        assertThat(basin).containsExactlyInAnyOrder(new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(0, 1));
    }

    @Test
    void findTopRightBasin() {
        final Integer[][] map = readString(SMALL_EXAMPLE);

        final Set<Coordinate> basin = findBasinAround(9, 0, map);

        assertThat(basin).containsExactlyInAnyOrder(
                new Coordinate(5, 0), new Coordinate(6, 0), new Coordinate(7, 0), new Coordinate(8, 0),
                new Coordinate(9, 0),
                new Coordinate(6, 1), new Coordinate(8, 1), new Coordinate(9, 1),
                new Coordinate(9, 2));
    }

    @Test
    void findMiddleBasin() {
        final Integer[][] map = readString(SMALL_EXAMPLE);

        final Set<Coordinate> basin = findBasinAround(2, 2, map);

        assertThat(basin.size()).isEqualTo(14);
    }

    @Test
    void findSampleBasins() {

    }

    private Set<Coordinate> findBasinAround(final int x, final int y, final Integer[][] map) {
        final Map<Coordinate, Boolean> alreadyProcessed = new HashMap<>();
        final Coordinate current = new Coordinate(x, y);
        final Set<Coordinate> result = new HashSet<>();
        result.add(current);
        boolean continueProcessing = true;
        while (continueProcessing) {
            final Set<Coordinate> newPositions = new HashSet<>();
            for (final Coordinate coord : result) {
                if (!alreadyProcessed.getOrDefault(coord, false)) {
                    newPositions.addAll(coord.neighboursInMap(map));
                    alreadyProcessed.put(coord, true);
                }
            }
            if (newPositions.isEmpty()) {
                continueProcessing = false;
            } else {
                result.addAll(newPositions);
            }
        }
        return result;
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

        @Override
        public String toString() {
            return "(" +
                    "x=" + x +
                    ", y=" + y +
                    ')';
        }
    }
}
