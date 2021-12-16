package day15;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import common.Coordinate;

import static org.assertj.core.api.Assertions.assertThat;

class Day15 {

    private static final String SMALL_EXAMPLE = """
            1163751742
            1381373672
            2136511328
            3694931569
            7463417111
            1319128137
            1359912421
            3125421639
            1293138521
            2311944581""";

    private static final String TINY_EXAMPLE = """
            116
            138
            213""";

    private Integer[][] readFile() {
        var inputStream = Day15.class.getResourceAsStream("/day15.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        final Stream<Integer[]> stream = buf.lines().map(line -> toArray(line.chars()));
        final List<Integer[]> collect = stream.collect(Collectors.toList());
        return collect.toArray(new Integer[collect.size()][]);
    }

    private Integer[][] readExample(final String EXAMPLE) {
        final Stream<Integer[]> stream = EXAMPLE.lines().map(line -> toArray(line.chars()));
        final List<Integer[]> collect = stream.collect(Collectors.toList());
        return collect.toArray(new Integer[collect.size()][]);
    }

    @Test
    void exampleFull() {
        assertThat(cheapestPath(readExample(SMALL_EXAMPLE))).isEqualTo(40);
    }

    @Test
    void exampleTiny() {
        assertThat(cheapestPath(readExample(TINY_EXAMPLE))).isEqualTo(7);
    }

    @Test
    void puzzleFull() {
        assertThat(cheapestPath(readFile())).isEqualTo(441);
    }

    @Test
    void puzzleFullSecondPart() {
        final Integer[][] inputGrid = readFile();
        final Integer[][] fullGrid = new Integer[inputGrid.length * 5][];
        for (int y = 0; y < fullGrid.length; y++) {
            fullGrid[y] = new Integer[fullGrid.length];
        }
        for (int yFactor = 0; yFactor < 5; yFactor++) {
            for (int xFactor = 0; xFactor < 5; xFactor++) {
                final var originalDimension = inputGrid.length;
                for (int y = 0; y < originalDimension; y++) {
                    for (int x = 0; x < originalDimension; x++) {
                        int newRiskValue = (inputGrid[y][x] + xFactor + yFactor);
                        if (newRiskValue > 9) {
                            newRiskValue %= 9;
                        }
                        fullGrid[yFactor * originalDimension + y][xFactor * originalDimension + x] = newRiskValue;
                    }
                }
            }
        }
        assertThat(cheapestPath(fullGrid)).isEqualTo(2849);
    }

    private long cheapestPath(final Integer[][] caveGrid) {
        final var costMap = new HashMap<Coordinate, Long>();
        costMap.put(new Coordinate(0, 0), 0L);
        long previousTargetCost = targetCost(caveGrid, costMap);
        long thisTargetCost = 0L;
        while (previousTargetCost != thisTargetCost) {
            for (int y = 0; y < caveGrid.length; y++) {
                for (int x = 0; x < caveGrid[0].length; x++) {
                    final Coordinate currentCoordinate = new Coordinate(x, y);
                    final var costToReachCurrent = costMap.get(currentCoordinate);
                    currentCoordinate.orthogonalNeighboursInMap(caveGrid)
                            .forEach(
                                    coord -> {
                                        final var currentMinForCoord = costMap.getOrDefault(coord, Long.MAX_VALUE);
                                        costMap.put(coord, Math.min(entryCost(coord, caveGrid) + costToReachCurrent,
                                                currentMinForCoord));
                                    }
                            );
                }
            }
            previousTargetCost = thisTargetCost;
            thisTargetCost = targetCost(caveGrid, costMap);
            System.out.println("Target cost (now/prev): (" + thisTargetCost + "/" + previousTargetCost + ")");
        }
        return previousTargetCost;
    }

    private Long targetCost(final Integer[][] caveGrid, final HashMap<Coordinate, Long> costMap) {
        return costMap.getOrDefault(new Coordinate(caveGrid[0].length - 1, caveGrid.length - 1), Long.MAX_VALUE);
    }

    private int entryCost(final Coordinate coord, final Integer[][] caveGrid) {
        if (coord.yValue() < caveGrid.length && coord.xValue() < caveGrid[0].length) {
            return caveGrid[coord.yValue()][coord.xValue()];
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private Integer[] toArray(IntStream input) {
        final List<Integer> collect = input.map(i -> i - '0')
                .boxed()
                .collect(Collectors.toList());
        return collect.toArray(new Integer[collect.size()]);
    }
}
