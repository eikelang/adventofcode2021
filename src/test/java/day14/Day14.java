package day14;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class Day14 {

    private static final String SMALL_EXAMPLE = """
            NNCB
                        
            CH -> B
            HH -> N
            CB -> H
            NH -> C
            HB -> C
            HC -> B
            HN -> C
            NN -> C
            BH -> H
            NC -> B
            NB -> B
            BN -> B
            BB -> N
            BC -> B
            CC -> N
            CN -> C""";

    private Stream<String> readFile() {
        var inputStream = Day14.class.getResourceAsStream("/day14.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    private Stream<String> readExample(final String example) {
        return example.lines();
    }

    @Test
    void sampleOneStep() {
        var input = inputFrom(readExample(SMALL_EXAMPLE));
        var mapping = readMapping(readExample(SMALL_EXAMPLE));
        assertThat(makeStep(input, mapping))
                .isEqualTo("NCNBCHB");
    }

    @Test
    void sampleFourSteps() {
        var input = inputFrom(readExample(SMALL_EXAMPLE));
        var mapping = readMapping(readExample(SMALL_EXAMPLE));
        assertThat(makeSteps(input, mapping, 4))
                .isEqualTo("NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB");
    }

    @Test
    void puzzleResult1() {
        var input = inputFrom(readFile());
        var mapping = readMapping(readFile());

        final LongSummaryStatistics longSummaryStatistics = makeSteps(input, mapping, 10)
                .chars()
                .boxed()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .values()
                .stream()
                .mapToLong(l -> l)
                .summaryStatistics();

        assertThat(longSummaryStatistics.getMax() - longSummaryStatistics.getMin()).isEqualTo(3259);
    }

    @Test
    void testInputToCharacterCounterSingleStep() {
        var input = inputFrom(readExample(SMALL_EXAMPLE));
        var mapping = readMapping(readExample(SMALL_EXAMPLE));

        final CountingReplicator countingReplicator = new CountingReplicator(mapping, input);
        countingReplicator.polymerize();

        assertThat(countingReplicator.pairCounters)
                .containsOnly(
                        entry("NN", 0L),
                        entry("NC", 1L),
                        entry("CN", 1L),
                        entry("NB", 1L),
                        entry("BC", 1L),
                        entry("CH", 1L),
                        entry("HB", 1L),
                        entry("CB", 0L));

        assertThat(countingReplicator.characterCounters)
                .containsOnly(
                        entry(Integer.valueOf('N'), 2L),
                        entry(Integer.valueOf('C'), 2L),
                        entry(Integer.valueOf('B'), 2L),
                        entry(Integer.valueOf('H'), 1L));
    }

    @Test
    void testInputToCharacterCounterTwoSteps() {
        var input = inputFrom(readExample(SMALL_EXAMPLE));
        var mapping = readMapping(readExample(SMALL_EXAMPLE));

        final CountingReplicator countingReplicator = new CountingReplicator(mapping, input);
        countingReplicator.polymerize();
        countingReplicator.polymerize();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(countingReplicator.pairCounters)
                    .containsOnly(
                            entry("NB", 2L),
                            entry("BC", 2L),
                            entry("CC", 1L),
                            entry("CN", 1L),
                            entry("BB", 2L),
                            entry("CB", 2L),
                            entry("BH", 1L),
                            entry("HC", 1L),
                            entry("NN", 0L),
                            entry("NC", 0L),
                            entry("CH", 0L),
                            entry("HB", 0L));

            softly.assertThat(countingReplicator.characterCounters)
                    .containsOnly(
                            entry(Integer.valueOf('N'), 2L),
                            entry(Integer.valueOf('C'), 4L),
                            entry(Integer.valueOf('B'), 6L),
                            entry(Integer.valueOf('H'), 1L));
        });
    }

    @Test
    void testInputToCharacterCounter() {
        var input = inputFrom(readExample(SMALL_EXAMPLE));
        var mapping = readMapping(readExample(SMALL_EXAMPLE));

        final CountingReplicator countingReplicator = new CountingReplicator(mapping, input);
        for (int i = 0; i < 10; i++) {
            countingReplicator.polymerize();
        }

        assertThat(countingReplicator.puzzleResult()).isEqualTo(1588);
    }

    @Test
    void testPuzzle2ToCharacterCounter() {
        var input = inputFrom(readFile());
        var mapping = readMapping(readFile());

        final CountingReplicator countingReplicator = new CountingReplicator(mapping, input);
        for (int i = 0; i < 40; i++) {
            countingReplicator.polymerize();
        }

        assertThat(countingReplicator.puzzleResult()).isEqualTo(3459174981021L);
    }

    private class CountingReplicator {

        private final Map<String, Set<String>> pairMapping;
        private final Map<String, Long> pairCounters = new HashMap<>();
        private final Map<Integer, Long> characterCounters;

        private CountingReplicator(final Map<String, Set<String>> pairMapping, final String input) {
            this.pairMapping = pairMapping;
            for (int i = 0; i < input.length(); i++) {
                final var pair = input.substring(i, Math.min(input.length(), i + 2));
                if (pair.length() == 2) {
                    pairCounters.put(pair, pairCounters.getOrDefault(pair, 0L) + 1L);
                }
            }
            characterCounters = input.chars().boxed().collect(Collectors.groupingBy(i -> i, Collectors.counting()));
        }

        public void polymerize() {
            final Set<StepDelta> stepDeltaStream = pairCounters.entrySet().stream()
                    .filter(e -> e.getValue() > 0)
                    .map(e -> stepSingleMapping(e.getKey(), e.getValue())).collect(Collectors.toSet());

            stepDeltaStream.forEach(System.out::println);

            stepDeltaStream.forEach(
                    delta -> {
                        delta.applyCharactDelta(characterCounters);
                        delta.applyPairDelta(pairCounters);
                    }
            );

            System.out.println();
        }

        public long puzzleResult() {
            final LongSummaryStatistics longSummaryStatistics =
                    characterCounters.values().stream().mapToLong(l -> l).summaryStatistics();
            return longSummaryStatistics.getMax() - longSummaryStatistics.getMin();
        }

        private StepDelta stepSingleMapping(final String pair, final long frequency) {
            final Map<String, Long> pairDeltaCounters = new HashMap<>();
            // polymerization splits the pair
            pairDeltaCounters.put(pair, -1L * frequency);

            final String addedElement = String.join("", pairMapping.get(pair));
            final Map<Integer, Long> characterDeltaCounters = new HashMap<>();
            characterDeltaCounters.put(0 + addedElement.charAt(0), frequency);

            // in place of one previous pair, we now get two new pairs for the next iteration
            final var leftPair = pair.substring(0, 1) + addedElement;
            final var rightPair = addedElement + pair.substring(1);

            pairDeltaCounters.put(leftPair, pairDeltaCounters.getOrDefault(leftPair,0L) + frequency);
            pairDeltaCounters.put(rightPair, pairDeltaCounters.getOrDefault(rightPair, 0L) + frequency);

            return new StepDelta(pairDeltaCounters, characterDeltaCounters);
        }

        private class StepDelta {

            private final Map<String, Long> pairDelta;
            private final Map<Integer, Long> characterDelta;

            private StepDelta(final Map<String, Long> pairDelta,
                    final Map<Integer, Long> characterDelta) {
                this.pairDelta = pairDelta;
                this.characterDelta = characterDelta;
            }

            void applyPairDelta(final Map<String, Long> pairCountersGlobal) {
                pairDelta.forEach(
                        (key, value) -> pairCountersGlobal.put(key, pairCountersGlobal.getOrDefault(key, 0L) + value));
            }

            void applyCharactDelta(final Map<Integer, Long> characterCountersGlobal) {
                characterDelta.forEach(
                        (key, value) -> characterCountersGlobal.put(key,
                                characterCountersGlobal.getOrDefault(key, 0L) + value));
            }

            @Override
            public String toString() {
                return "StepDelta{"
                + pairDelta + ","
                + characterDelta
                        + "}";
            }
        }
    }

    private String makeSteps(final String input, final Map<String, Set<String>> mapping, final int steps) {
        var lastResult = input;
        for (int i = 0; i < steps; i++) {
            lastResult = makeStep(lastResult, mapping);
        }
        return lastResult;
    }

    private String makeStep(final String input, final Map<String, Set<String>> mapping) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            final var prefix = input.substring(i, Math.min(i + 2, input.length()));
            if (prefix.length() == 2) {
                result.append(prefix.charAt(0));
                result.append(String.join("", mapping.get(prefix)));
            } else {
                result.append(prefix);
            }
        }
        return result.toString();
    }

    @Test
    void testReadInput() {
        assertThat(inputFrom(readExample(SMALL_EXAMPLE)))
                .isEqualTo("NNCB");
    }

    @Test
    void testReadMappingFromSample() {
        assertThat(readMapping(readExample(SMALL_EXAMPLE)))
                .containsEntry("CH", singleton("B"))
                .containsEntry("HH", singleton("N"))
                .containsEntry("CB", singleton("H"))
                .containsEntry("NH", singleton("C"))
                .containsEntry("HB", singleton("C"))
                .containsEntry("HC", singleton("B"))
                .containsEntry("HN", singleton("C"))
                .containsEntry("NN", singleton("C"))
                .containsEntry("BH", singleton("H"))
                .containsEntry("NC", singleton("B"))
                .containsEntry("NB", singleton("B"))
                .containsEntry("BN", singleton("B"))
                .containsEntry("BB", singleton("N"))
                .containsEntry("BC", singleton("B"))
                .containsEntry("CC", singleton("N"))
                .containsEntry("CN", singleton("C"));
    }

    private String inputFrom(final Stream<String> input) {
        return input.takeWhile(l -> !l.isEmpty()).collect(Collectors.joining());
    }

    private Map<String, Set<String>> readMapping(final Stream<String> input) {
        return input.dropWhile(l -> !l.contains("->"))
                .map(s -> s.split("->"))
                .collect(Collectors.groupingBy(a -> a[0].trim(),
                        Collectors.mapping(a -> a[1].trim(), Collectors.toSet())));
    }

}
