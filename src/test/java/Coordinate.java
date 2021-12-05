import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Coordinate {

    private final int x;
    private final int y;

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

    private Coordinate(final int x, final int y) {

        this.x = x;
        this.y = y;
    }

    public static Coordinate of(final int x, final int y) {
        return new Coordinate(x, y);
    }

    public Stream<Coordinate> lineTo(final Coordinate end) {
        if (x == end.x) {
            return IntStream.range(Math.min(y, end.y), Math.max(y, end.y) + 1)
                    .mapToObj(y -> Coordinate.of(x, y));
        } else if (y == end.y) {
            return IntStream.range(Math.min(x, end.x), Math.max(x, end.x) + 1)
                    .mapToObj(x -> Coordinate.of(x, y));

        }
        return Stream.empty();
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public Stream<Coordinate> lineWithDiagonalsTo(final Coordinate end) {
        if (x == end.x || y == end.y) {
            return lineTo(end);
        } else if (x < end.x && y < end.y) {
            final Stream.Builder<Coordinate> builder = Stream.builder();
            for (int startX = x, startY = y; startX <= end.x && startY <= end.y; startX++, startY++) {
                builder.accept(new Coordinate(startX, startY));
            }
            return builder.build();
        } else if (x > end.x && y > end.y) {
            return end.lineWithDiagonalsTo(this);
        } else if (x < end.x && y > end.y) {
            final Stream.Builder<Coordinate> builder = Stream.builder();
            for (int startX = x, startY = y; startX <= end.x && startY >= end.y; startX++, startY--) {
                builder.accept(new Coordinate(startX, startY));
            }
            return builder.build();
        }
        return end.lineWithDiagonalsTo(this);
    }
}
