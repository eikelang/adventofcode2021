import java.util.stream.Stream;

public class LineParser {

    private final Coordinate start;
    private final Coordinate end;

    public LineParser(final String input) {
        final String[] coordinatesAsText = input.split("->");
        final String[] startCoordinate = coordinatesAsText[0].trim().split(",");
        start = Coordinate.of(Integer.parseInt(startCoordinate[0].trim()), Integer.parseInt(startCoordinate[1].trim()));
        final String[] endCoordinate = coordinatesAsText[1].trim().split(",");
        end = Coordinate.of(Integer.parseInt(endCoordinate[0].trim()), Integer.parseInt(endCoordinate[1].trim()));

    }

    public Stream<Coordinate> fullLine() {
        return start.lineTo(end);
    }

    public Stream<Coordinate> fullLineSupportingDiagonals() {
        return start.lineWithDiagonalsTo(end);
    }
}
