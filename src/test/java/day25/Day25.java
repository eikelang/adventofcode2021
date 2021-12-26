package day25;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import day24.Day24;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class Day25 {

    private static final String SAMPLE_INITIAL = """
            ...>...
            .......
            ......>
            v.....>
            ......>
            .......
            ..vvv..""";

    private static final String SAMPLE_AFTER_SIDE = """
            ....>..
            .......
            >......
            v.....>
            >......
            .......
            ..vvv..""";

    private static final String SAMPLE_AFTER_ONE = """
            ..vv>..
            .......
            >......
            v.....>
            >......
            .......
            ....v..""";

    private static final String SAMPLE_FULL = """
            v...>>.vv>
            .vv>>.vv..
            >>.>v>...v
            >>v>>.>.v.
            v>v.vv.v..
            >.>>..v...
            .vv..>.>v.
            v.v..>>v.v
            ....v..v.>""";

    private static final String SAMPLE_FINAL = """
            ..>>v>vv..
            ..v.>>vv..
            ..>>v>>vv.
            ..>>>>>vv.
            v......>vv
            v>v....>>v
            vvv.....>>
            >vv......>
            .>v.vv.v..""";

    private Cucumbers cucumbers;

    @BeforeEach
    void setUp() {
        cucumbers = new Cucumbers();
    }

    @Test
    void singleMoveInLine() {
        assertThat(cucumbers.stepForLine("...>>>>>...")).isEqualTo("...>>>>.>..");
    }

    @Test
    void multipleMovesInLine() {
        assertThat(cucumbers.stepForLine("...>>>>.>..")).isEqualTo("...>>>.>.>.");
    }

    @Test
    void lineWrapsAroundEnd() {
        assertThat(cucumbers.stepForLine(".vv.v..>>>")).isEqualTo(">vv.v..>>.");
    }

    @Test
    @DisplayName("Don't wrap around end if the beginning only becomes free as the result of a simultaneous move")
    void wrapEdgeCase() {
        assertThat(cucumbers.stepForLine(">...>")).isEqualTo(".>..>");
    }

    @Test
    void singleMoveForColumn() {
        assertThat(cucumbers.stepForColum("...vvvvv...")).isEqualTo("...vvvv.v..");
    }

    @Test
    void multipleMovesInColumn() {
        assertThat(cucumbers.stepForColum("...vvvv.v..")).isEqualTo("...vvv.v.v.");
    }

    @Test
    void columnWrapsAroundEnd() {
        assertThat(cucumbers.stepForColum(".vv.v..vvv")).isEqualTo("vv.v.v.vv.");
    }

    @Test
    void sampleOneStep() {
        final var afterSideSteps = SAMPLE_INITIAL.lines().map(cucumbers::stepForLine).toList();
        final var visualIntermediateResult = print(afterSideSteps);
        assertThat(visualIntermediateResult).isEqualTo(SAMPLE_AFTER_SIDE);
        final var transposed = transpose(afterSideSteps);
        final var transposedAfterColumnStep = transposed.stream().map(cucumbers::stepForColum).toList();
        final var restored = transpose(transposedAfterColumnStep);
        final var visualResult = print(restored);
        assertThat(visualResult).isEqualTo(SAMPLE_AFTER_ONE);
    }

    @Test
    void sampleFull() {
        String visualResult = "";
        var input = SAMPLE_FULL.lines().toList();
        var previous = "";
        var keepGoing = true;
        int i = 0;
        for (; keepGoing; i++) {
            previous = visualResult;
            final var afterSideSteps = input.stream().map(cucumbers::stepForLine).toList();
            final var visualIntermediateResult = print(afterSideSteps);
            final var transposed = transpose(afterSideSteps);
            final var transposedAfterColumnStep = transposed.stream().map(cucumbers::stepForColum).toList();
            input = transpose(transposedAfterColumnStep);
            visualResult = print(input);
            keepGoing = !visualResult.equals(previous);
        }
        assertThat(i).isEqualTo(58);
        assertThat(visualResult).isEqualTo(SAMPLE_FINAL);
    }

    @Test
    void puzzleFull() {
        String visualResult = "";
        var input = readFile().toList();
        var previous = "";
        var keepGoing = true;
        int i = 0;
        for (; keepGoing; i++) {
            previous = visualResult;
            final var afterSideSteps = input.stream().map(cucumbers::stepForLine).toList();
            final var visualIntermediateResult = print(afterSideSteps);
            final var transposed = transpose(afterSideSteps);
            final var transposedAfterColumnStep = transposed.stream().map(cucumbers::stepForColum).toList();
            input = transpose(transposedAfterColumnStep);
            visualResult = print(input);
            keepGoing = !visualResult.equals(previous);
        }
        final var totalCount = i;
        final var finalVisual = visualResult;
        assertThat(totalCount).isEqualTo(308);
    }

    private Stream<String> readFile() {
        var inputStream = Day24.class.getResourceAsStream("/day25.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    @Test
    void testTranspose1() {
        var input = """
                >>>
                ...
                ...""";

        assertThat(transpose(input.lines().toList())).containsExactly(">..", ">..", ">..");
    }

    @Test
    void testTransposeIdentity() {
        var input = """
                >>>
                ...
                ...""";

        assertThat(transpose(transpose(input.lines().toList()))).containsExactly(">>>", "...", "...");
    }

    @Test
    void testPrint() {
        assertThat(print(asList(">>>", "...", "..."))).isEqualTo("""
                >>>
                ...
                ...""");
    }

    private String print(final List<String> restored) {
        return restored.stream().collect(Collectors.joining("\n"));
    }

    private List<String> transpose(final List<String> linesToTranspose) {
        final var width = linesToTranspose.stream().mapToInt(String::length).max().orElse(0);
        return IntStream.range(0, width)
                .mapToObj(i -> linesToTranspose.stream()
                        .map(line -> line.substring(i, i + 1)).collect(
                                Collectors.joining())).toList();
    }

    private class Cucumbers {

        public String stepForLine(final String line) {
            return stepForDirection(line, ">");
        }

        private String stepForDirection(final String lineOrColumn, final String direction) {
            final var mustWrap = lineOrColumn.matches("^\\..*" + direction + "$");
            final var afterRegularReplacement = lineOrColumn
                    .replaceAll(direction + "\\.", "." + direction);
            if (mustWrap) {
                return direction + afterRegularReplacement.substring(1, afterRegularReplacement.length() - 1) + ".";
            }
            return afterRegularReplacement;
        }

        public String stepForColum(final String column) {
            return stepForDirection(column, "v");
        }
    }
}
