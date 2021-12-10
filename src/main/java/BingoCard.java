import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BingoCard {

    private final Set<Set<String>> rowsAndColumns = new HashSet<>();
    private int lastCall;

    public BingoCard(final String input) {
        final List<List<String>> lines = input.lines()
                .map(String::strip)
                .map(line -> line.split("\\h+"))
                .map(Arrays::asList)
                .map(ArrayList::new)
                .collect(Collectors.toCollection(ArrayList::new));

        lines.forEach(line -> rowsAndColumns.add(new HashSet<>(line)));

        IntStream.range(0, lines.size()) // we know/assume the cards are square
                .mapToObj(i -> nthColumnForLines(i, lines))
                .forEach(rowsAndColumns::add);

    }

    public boolean bingo() {
        return rowsAndColumns.stream().anyMatch(Set::isEmpty);
    }

    public void call(final String calledNumber) {
        lastCall = Integer.parseInt(calledNumber);
        for (final Set<String> line : rowsAndColumns) {
            line.remove(calledNumber);
        }
    }

    private Set<String> nthColumnForLines(final int columnNumber, final List<List<String>> lines) {
        return lines.stream()
                .flatMap(l -> l.stream().skip(columnNumber).limit(1))
                .collect(Collectors.toCollection(HashSet::new));
    }

    public int unmarkedSum() {
        return rowsAndColumns.stream().flatMap(Collection::stream).distinct().mapToInt(Integer::parseInt).sum();
    }

    public int value() {
        return lastCall * unmarkedSum();
    }
}