package day10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day10 {

    private static final String SMALL_EXAMPLE = """
            [({(<(())[]>[[{[]{<()<>>
            [(()[<>])]({[<{<<[]>>(
            {([(<{}[<>[]}>{[]{[(<()>
            (((({<>}<{<{<>}{[]{[]{}
            [[<[([]))<([[{}[[()]]]
            [{[{({}]{}}([{[{{{}}([]
            {<[[]]>}<{[{[{[]{()[[[]
            [<(<(<(<{}))><([]([]()
            <{([([[(<>()){}]>(<<{{
            <{([{{}}[<[[[<>{}]]]>[]]""";

    private static Stream<String> readFile() {
        final var inputStream = Day10.class.getResourceAsStream("/day10.txt");
        final var fis = new InputStreamReader(inputStream);
        final var buf = new BufferedReader(fis);
        return buf.lines();
    }

    @Test
    void invalidScoreForSample() {
        assertThat(SMALL_EXAMPLE.lines()
                .map(Parse::new)
                .filter(Parse::isCorrupt)
                .mapToLong(Parse::syntaxErrorScore)
                .sum())
                .isEqualTo(26397);
    }

    @Test
    void invalidScore() {
        assertThat(readFile()
                .map(Parse::new)
                .filter(Parse::isCorrupt)
                .mapToLong(Parse::syntaxErrorScore)
                .sum())
                .isEqualTo(392367);
    }

    @Test
    void completionScoreForSample() {
        final List<Long> scores = SMALL_EXAMPLE.lines()
                .map(Parse::new)
                .filter(Parse::isIncomplete)
                .mapToLong(Parse::completionScore)
                .sorted()
                .boxed()
                .collect(Collectors.toList());

        final var middleIndex = scores.size() / 2;

        assertThat(scores.get(middleIndex)).isEqualTo(288957);
    }

    @Test
    void completionScore() {
        final List<Long> allCompletionScores = readFile()
                .map(Parse::new)
                .filter(Parse::isIncomplete)
                .mapToLong(Parse::completionScore)
                .sorted()
                .boxed()
                .collect(Collectors.toList());
        final var middleIndex = allCompletionScores.size() / 2;

        assertThat(allCompletionScores.get(middleIndex)).isEqualTo(2192104158L);
    }
}
