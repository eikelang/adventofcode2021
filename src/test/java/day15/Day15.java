package day15;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import common.Coordinate;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
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

    private Integer[][] readExample(final String input) {
        final Stream<Integer[]> stream = input.lines().map(line -> toArray(line.chars()));
        final List<Integer[]> collect = stream.collect(Collectors.toList());
        return collect.toArray(new Integer[collect.size()][]);
    }

    @Test
    void exampleFull() {
        assertThat(cheapestPath(readExample(SMALL_EXAMPLE))).isEqualTo(40);
    }

    @Test
    void exampleSmallAStar() {
        final Integer[][] grid = readExample(SMALL_EXAMPLE);
        final List<Coordinate> cheapestPath = cheapestPathAStar(grid);
        assertThat(cheapestPath.stream()
                .filter(coord -> !coord.equals(new Coordinate(0,0)))
                .mapToLong(coordinate -> grid[coordinate.yValue()][coordinate.xValue()])
                .sum())
                .isEqualTo(40);
    }

    @Test
    void exampleTinyAStar() {
        final Integer[][] grid = readExample(TINY_EXAMPLE);
        final List<Coordinate> cheapestPath = cheapestPathAStar(grid);
        assertThat(cheapestPath.stream()
                .filter(coord -> !coord.equals(new Coordinate(0,0)))
                .mapToLong(coordinate -> grid[coordinate.yValue()][coordinate.xValue()])
                .sum())
                .isEqualTo(7);
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
    void puzzleFullAStar() {
        final Integer[][] grid = readFile();
        final List<Coordinate> cheapestPath = cheapestPathAStar(grid);
        assertThat(cheapestPath.stream()
                .filter(coord -> !coord.equals(new Coordinate(0,0)))
                .mapToLong(coordinate -> grid[coordinate.yValue()][coordinate.xValue()])
                .sum())
                .isEqualTo(441);
    }

    private List<Coordinate> cheapestPathAStar(final Integer[][] grid) {
        final var fScore = new HashMap<Coordinate, Long>();
        final var start = new Coordinate(0, 0);
        final var goal = new Coordinate(grid[0].length - 1, grid.length - 1);
        final var openSet = new PriorityQueue<Coordinate>(
                Comparator.comparing(coordinate -> fScore.getOrDefault(coordinate, Long.MAX_VALUE)));
        openSet.add(start);

        final var cameFrom = new HashMap<Coordinate, Coordinate>();
        final var gScore = new HashMap<Coordinate, Long>();
        gScore.put(start, 0L);
        final Function<Coordinate, Long> costToGoalHeuristic =
                coord -> grid.length * grid.length * 9L;
        fScore.put(start, costToGoalHeuristic.apply(start));

        while (!openSet.isEmpty()) {
            final var current = openSet.poll();
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            final Set<Coordinate> neighbours = current.orthogonalNeighboursInMap(grid);
            neighbours.forEach(neighbour -> {
                final var tentantiveGScore =
                        gScore.getOrDefault(current, Long.MAX_VALUE) + grid[neighbour.yValue()][neighbour.xValue()];
                if (tentantiveGScore < gScore.getOrDefault(neighbour, Long.MAX_VALUE)) {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, tentantiveGScore);
                    fScore.put(neighbour, tentantiveGScore + costToGoalHeuristic.apply(neighbour));
                    if (!openSet.contains(neighbour)) {
                        openSet.add(neighbour);
                    }
                }
            });
        }
        return emptyList();

    }

    private List<Coordinate> reconstructPath(final Map<Coordinate, Coordinate> cameFrom, final Coordinate current) {
        final var path = new ArrayDeque<>(singleton(current));
        var mutableCurrent = current;
        while (cameFrom.keySet().contains(mutableCurrent)) {
            mutableCurrent = cameFrom.get(mutableCurrent);
            path.push(mutableCurrent);
        }
        return path.stream().toList();
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
