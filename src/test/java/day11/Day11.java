package day11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import common.Coordinate;

import static org.assertj.core.api.Assertions.assertThat;

class Day11 {

    private static final String EXAMPLE = """
            5483143223
            2745854711
            5264556173
            6141336146
            6357385478
            4167524645
            2176841721
            6882881134
            4846848554
            5283751526""";

    private Integer[][] readFile() {
        var inputStream = Day11.class.getResourceAsStream("/day11.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        final Stream<Integer[]> stream = buf.lines().map(line -> toArray(line.chars()));
        final List<Integer[]> collect = stream.collect(Collectors.toList());
        return collect.toArray(new Integer[collect.size()][]);
    }

    private Integer[][] readExample() {
        return readString(EXAMPLE);
    }

    private Integer[][] readString(final String input) {
        final Stream<Integer[]> stream = input.lines().map(line -> toArray(line.chars()));
        final List<Integer[]> collect = stream.collect(Collectors.toList());
        return collect.toArray(new Integer[collect.size()][]);
    }

    @Test
    void noFlashesOnFirstStep() {
        assertThat(flashesFor(readExample(), 1).sum()).isZero();
    }

    @Test
    void twoSteps() {
        assertThat(flashesFor(readExample(), 2).sum()).isEqualTo(35);
    }

    @Test
    void threeStepsIndividually() {
        assertThat(flashesFor(readExample(), 3))
                .containsExactly(0L, 35L, 45L);
    }

    @Test
    void tenSteps() {
        assertThat(flashesFor(readExample(), 10).sum()).isEqualTo(204);
    }

    @Test
    void oneHundredStepsSample() {
        assertThat(flashesFor(readExample(), 100).sum()).isEqualTo(1656);
    }

    @Test
    void oneHundredStepsForReal() {
        assertThat(flashesFor(readFile(), 100).sum()).isEqualTo(1697);
    }

    @Test
    void findFirstSync() {
        final Integer[][] octopusGrid = readFile();
        final var gridSize = octopusGrid.length * octopusGrid[0].length;
        assertThat(LongStream.rangeClosed(1L, 5000L)
                .dropWhile(counter -> advanceStep(octopusGrid) < gridSize)
                .findFirst())
                .isEqualTo(344);
    }

    @Test
    void cornerNeighbours() {
        final var grid = """
                000
                000
                000""";
        final Integer[][] arrayGrid = readString(grid);

        assertThat(new Coordinate(0, 0).neighboursInMap(arrayGrid))
                .containsExactlyInAnyOrder(new Coordinate(0, 1), new Coordinate(1, 0), new Coordinate(1, 1));
    }

    @Test
    void centerNeighbours() {
        final var grid = """
                000
                000
                000""";
        final Integer[][] arrayGrid = readString(grid);

        assertThat(new Coordinate(1, 1).neighboursInMap(arrayGrid))
                .containsExactlyInAnyOrder(new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(2, 0),
                        new Coordinate(0, 1), new Coordinate(2, 1),
                        new Coordinate(0, 2), new Coordinate(1, 2), new Coordinate(2, 2));
    }

    @Test
    void sideNeighbours() {
        final var grid = """
                000
                000
                000""";
        final Integer[][] arrayGrid = readString(grid);

        assertThat(new Coordinate(1, 0).neighboursInMap(arrayGrid))
                .containsExactlyInAnyOrder(new Coordinate(0, 0), new Coordinate(2, 0), new Coordinate(0, 1),
                        new Coordinate(1, 1), new Coordinate(2, 1));
    }

    private LongStream flashesFor(final Integer[][] octopusGrid, final int steps) {
        System.out.println("Initial state");
        printGrid(octopusGrid);
        return LongStream.rangeClosed(1, steps)
                .map(l -> advanceStep(octopusGrid));
    }

    private long advanceStep(final Integer[][] octopusGrid) {
        final Boolean[][] flashGrid = createFlashGrid(octopusGrid);
        increaseEnergyLevel(octopusGrid);
        System.out.println("After level increase");
        printGrid(octopusGrid);
        final long flashesAdded = handleFlashes(octopusGrid, flashGrid);
        System.out.println("Flashes");
        printFlashGrid(flashGrid);
        resetCounters(octopusGrid);
        System.out.println("At end of Step");
        printGrid(octopusGrid);
        return flashesAdded;
    }

    private void resetCounters(final Integer[][] octopusGrid) {
        for (int y = 0; y < octopusGrid.length; y++) {
            for (int x = 0; x < octopusGrid[0].length; x++) {
                if (octopusGrid[y][x] > 9) {
                    octopusGrid[y][x] = 0;
                }
            }
        }
    }

    private long handleFlashes(final Integer[][] octopusGrid, final Boolean[][] flashGrid) {

        var totalFlashesForStep = 0L;
        var flashesAdded = 0;
        do {
            flashesAdded = 0;
            for (int y = 0; y < octopusGrid.length; y++) {
                for (int x = 0; x < octopusGrid[0].length; x++) {
                    if (octopusGrid[y][x] > 9 && !flashGrid[y][x]) {
                        flashesAdded++;
                        flashGrid[y][x] = Boolean.TRUE;
                        final Set<Coordinate> coordinates = new Coordinate(x, y).neighboursInMap(octopusGrid);
                        for (final Coordinate coord : coordinates) {
                            octopusGrid[coord.yValue()][coord.xValue()] += 1;
                        }
                    }
                }
            }
            totalFlashesForStep += flashesAdded;
        } while (flashesAdded > 0);
        return totalFlashesForStep;
    }

    private void increaseEnergyLevel(final Integer[][] octopusGrid) {
        for (int y = 0; y < octopusGrid.length; y++) {
            for (int x = 0; x < octopusGrid[0].length; x++) {
                octopusGrid[y][x] += 1;
            }
        }
    }

    private Boolean[][] createFlashGrid(final Integer[][] octopusGrid) {
        final Boolean[][] flashGrid = new Boolean[octopusGrid.length][];
        for (int y = 0; y < flashGrid.length; y++) {
            flashGrid[y] = new Boolean[octopusGrid[0].length];
            for (int x = 0; x < flashGrid[y].length; x++) {
                flashGrid[y][x] = Boolean.FALSE;
            }
        }
        return flashGrid;
    }

    private Integer[] toArray(IntStream input) {
        final List<Integer> collect = input.map(i -> i - '0')
                .boxed()
                .collect(Collectors.toList());
        return collect.toArray(new Integer[collect.size()]);
    }

    void printGrid(final Integer[][] octopusGrid) {
        for (final var line : octopusGrid) {
            System.out.println(Arrays.stream(line).map(Object::toString).collect(Collectors.joining("")));
        }
        System.out.println();
    }

    void printFlashGrid(final Boolean[][] flashGrid) {
        for (final var line : flashGrid) {
            System.out.println(Arrays.stream(line).map(l -> l ? "*" : " ").collect(Collectors.joining("")));
        }
        System.out.println();
    }
}
