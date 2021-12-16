package common;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public class Coordinate {

    private int x;
    private int y;

    public Coordinate(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public Set<Coordinate> neighboursInMap(final Integer[][] map) {
        final List<Integer> xValues =
                IntStream.rangeClosed(x - 1, x + 1)
                        .filter(i -> i >= 0 && i < map[0].length)
                        .boxed().toList();
        final List<Integer> yValues =
                IntStream.rangeClosed(y - 1, y + 1)
                        .filter(i -> i >= 0 && i < map.length)
                        .boxed()
                        .toList();
        final Set<Coordinate> neighbourhood = new HashSet<>();
        for (int yi : yValues) {
            for (int xi : xValues) {
                if (xi != x || yi != y) {
                    neighbourhood.add(new Coordinate(xi, yi));
                }
            }
        }
        return neighbourhood;
    }

    public Set<Coordinate> orthogonalNeighboursInMap(final Integer[][] map) {
        final List<Integer> xValues =
                IntStream.of(x - 1, x + 1)
                        .filter(i -> i >= 0 && i < map[0].length)
                        .boxed().toList();
        final List<Integer> yValues =
                IntStream.of(y - 1, y + 1)
                        .filter(i -> i >= 0 && i < map.length)
                        .boxed()
                        .toList();
        final Set<Coordinate> neighbourhood = new HashSet<>();
        for (int yi : yValues) {
            neighbourhood.add(new Coordinate(x, yi));
        }
        for (int xi : xValues) {
            neighbourhood.add(new Coordinate(xi, y));
        }

        return neighbourhood;
    }

    public Coordinate rightNeighbour() {
        return new Coordinate(this.x + 1, this.y);
    }

    public Coordinate bottomNeighbour() {
        return new Coordinate(this.x, this.y + 1);
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

    public int xValue() {
        return x;
    }

    public int yValue() {
        return y;
    }
}
