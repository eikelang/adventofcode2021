import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day10 {

    private Set<Character> OPENERS = new HashSet<>(Arrays.asList('(', '[', '{', '<'));

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

    private Stream<String> readFile() {
        var inputStream = Day10.class.getResourceAsStream("/day10.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    @Test
    void testCorruptLine() {
        assertThat(firstCorruptCharacter("{([(<{}[<>[]}>{[]{[(<()>"))
                .containsExactly('}');
    }

    @Test
    void allIn() {
        assertThat(readFile().flatMap(this::firstCorruptCharacter)
                .mapToInt(this::scoreForCharacter)
                .sum())
                .isEqualTo(392367);
    }

    @Test
    void completionStringSingleLine() {
        assertThat(completionString("[({(<(())[]>[[{[]{<()<>>")).isEqualTo("}}]])})]");
    }

    @Test
    void completionStringScore() {
        assertThat(completionScore("}}]])})]"))
                .isEqualTo(288957);
    }

    @Test
    void allCompletionStringScores() {

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(completionScore("}}]])})]")).isEqualTo(288957);
            softly.assertThat(completionScore(")}>]})")).isEqualTo(5566);
            softly.assertThat(completionScore("}}>}>))))")).isEqualTo(1480781);
            softly.assertThat(completionScore("]]}}]}]}>")).isEqualTo(995444);
            softly.assertThat(completionScore("])}>")).isEqualTo(294);
        });
    }

    @Test
    void completeAll() {
        final List<Long> allCompletionScores = readFile().filter(this::isIncomplete)
                .map(this::completionString)
                .mapToLong(this::completionScore)
                .sorted()
                .boxed()
                .collect(Collectors.toList());
        var middleIndex = allCompletionScores.size() / 2;

        assertThat(allCompletionScores.get(middleIndex)).isEqualTo(2192104158L);
    }

    @Test
    void completeSample() {
        final List<Long> scores = SMALL_EXAMPLE.lines().filter(this::isIncomplete)
                .map(this::completionString)
                .mapToLong(this::completionScore)
                .sorted()
                .boxed()
                .collect(Collectors.toList());

        var middleIndex = scores.size() / 2;

        assertThat(scores.get(middleIndex)).isEqualTo(288957);
    }

    @Test
    void filterIncomplete() {
        assertThat(SMALL_EXAMPLE.lines().filter(this::isIncomplete))
                .containsExactlyInAnyOrder("[({(<(())[]>[[{[]{<()<>>",
                        "[(()[<>])]({[<{<<[]>>(",
                        "(((({<>}<{<{<>}{[]{[]{}",
                        "{<[[]]>}<{[{[{[]{()[[[]",
                        "<{([{{}}[<[[[<>{}]]]>[]]");
    }

    @Test
    void specialCase() {
        assertThat(isIncomplete("[(()[<>])]({[<{<<[]>>(")).isTrue();
    }

    @Test
    void completeSpecialCase() {
        assertThat(completionString("[(()[<>])]({[<{<<[]>>(")).isEqualTo(")}>]})");
    }

    private long completionScore(final String input) {
        return completionScore(input.substring(1), characterScore(input.charAt(0)));
    }

    private String completionString(final String input) {
        final Stack<Character> parseStack = new Stack<>();
        for (char c : input.toCharArray()) {
            if (isOpeningParen(c)) {
                parseStack.push(c);
            } else {
                var toClose = parseStack.peek();
                if (matches(toClose, c)) {
                    parseStack.pop();
                }
            }
        }
        var completion = "";
        while (!parseStack.isEmpty()) {
            completion += switch (parseStack.pop()) {
                case '(' -> ")";
                case '[' -> "]";
                case '{' -> "}";
                case '<' -> ">";
                default -> "";
            };
        }
        return completion;
    }

    private int characterScore(final char input) {
        return switch (input) {
            case ')' -> 1;
            case ']' -> 2;
            case '}' -> 3;
            case '>' -> 4;
            default -> 0;
        };
    }

    private long completionScore(final String input, long accumlator) {
        if (input.isEmpty()) {
            return accumlator;
        }
        return completionScore(input.substring(1), 5 * accumlator + characterScore(input.charAt(0)));
    }

    private boolean isIncomplete(String line) {
        return firstCorruptCharacter(line).findAny().isEmpty();
    }

    private Stream<Character> firstCorruptCharacter(final String line) {
        final Stack<Character> parseStack = new Stack<>();
        for (char c : line.toCharArray()) {
            if (isOpeningParen(c)) {
                parseStack.push(c);
            } else {
                var toClose = parseStack.pop();
                if (!matches(toClose, c)) {
                    return Stream.of(c);
                }
            }
        }
        return Stream.empty();
    }

    private int scoreForCharacter(char character) {
        return switch (character) {
            case ')' -> 3;
            case ']' -> 57;
            case '}' -> 1197;
            case '>' -> 25137;
            default -> 0;
        };
    }

    private boolean matches(char opener, char closer) {
        return switch (opener) {
            case '(' -> closer == ')';
            case '[' -> closer == ']';
            case '{' -> closer == '}';
            case '<' -> closer == '>';
            default -> false;
        };
    }

    private boolean isOpeningParen(final char paren) {
        return OPENERS.contains(paren);
    }

}
