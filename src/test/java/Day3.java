import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day3 {

    private static final String MINI_TEST = """
            00100
            11110
            10110
            10111
            10101
            01111
            00111
            11100
            10000
            11001
            00010
            01010""";

    private Stream<String> readFile() {
        var inputStream = Day3.class.getResourceAsStream("/day3.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    @Test
    void foldIt() {
        final long totalCount = readFile().count();
        final Optional<DatalineAccumulation> reduction = readFile()
                .map(DatalineAccumulation::new)
                .reduce(DatalineAccumulation::combine);
        System.out.println("Total lines " + totalCount);
        reduction.ifPresent(System.out::println);
        reduction.ifPresent(r -> System.out.println(r.toBinaryStringMostCommon()));
        reduction.ifPresent(r -> System.out.println(r.toBinaryStringLeastCommon()));
        assertThat(reduction.map(DatalineAccumulation::powerConsumption)).contains(4191876L);
    }

    @Test
    void filterIt() {
        final long totalCount = readFile().count();

        final int oxygenNumeric = oxygenValue(readFile().toList());
        final int scrubberRatingNumeric = scrubberRating(readFile().toList());

        assertThat(oxygenNumeric * scrubberRatingNumeric).isEqualTo(3414905L);
    }

    private int scrubberRating(final List<String> measurementCandidates) {
        return findAndParse(measurementCandidates, DatalineAccumulation::leastCommonPrefix);
    }

    private int oxygenValue(final List<String> measurementCandidates) {
        return findAndParse(measurementCandidates, DatalineAccumulation::mostCommonPrefix);
    }

    private int findAndParse(final List<String> measurementCandidates,
            final Function<DatalineAccumulation, String> mappingStrategy) {
        final String binaryStringForStrategy =
                isolateBinaryString(measurementCandidates, mappingStrategy);

        return Integer.parseInt(binaryStringForStrategy, 2);
    }

    @Test
    void miniFilter() {
        final long totalCount = readFile().count();

        final int oxygenNumeric = oxygenValue(MINI_TEST.lines().toList());
        final int scrubberRatingNumeric = scrubberRating(MINI_TEST.lines().toList());

        assertThat(oxygenNumeric * scrubberRatingNumeric).isEqualTo(230L);
    }

    private static String isolateBinaryString(final List<String> measurementCandidates,
            final Function<DatalineAccumulation, String> prefixMapper) {
        final var accumlatedPrefix = new StringBuilder();
        var result = measurementCandidates;
        while (result.size() > 1) {
            final var prefix = result.stream()
                    .map(DatalineAccumulation::new)
                    .reduce(DatalineAccumulation::combine)
                    .map(prefixMapper::apply)
                    .orElseThrow(() -> new RuntimeException("This shouldn´t happen!"));
            accumlatedPrefix.append(prefix);

            result = result.stream()
                    .filter(s -> s.startsWith(prefix))
                    .map(s -> s.substring(1))
                    .toList();
        }
        return accumlatedPrefix + result.get(0);
    }

    private static class DatalineAccumulation {
        private final int[] counters;
        private long totalCount = 1L;

        DatalineAccumulation(final String dataLine) {
            final var charArray = dataLine.toCharArray();
            counters = new int[charArray.length];
            for (int i = 0; i < dataLine.length(); i++) {
                counters[i] = charArray[i] - '0';
            }
        }

        private DatalineAccumulation(final int[] counters, final long totalCount) {
            this.counters = counters.clone();
            this.totalCount = totalCount;
        }

        DatalineAccumulation combine(final DatalineAccumulation other) {
            final int[] target = counters.clone();
            for (int i = 0; i < counters.length; i++) {
                target[i] += other.counters[i];
            }
            return new DatalineAccumulation(target, totalCount + 1);
        }

        private String toBinaryStringMostCommon() {
            final StringBuilder result = new StringBuilder();
            for (final int counter : counters) {
                result.append(counter >= (totalCount - counter) ? "1" : "0");
            }
            return result.toString();
        }

        private String toBinaryStringLeastCommon() {
            final StringBuilder result = new StringBuilder();
            for (final int counter : counters) {
                result.append(counter < (totalCount - counter) ? "1" : "0");
            }
            return result.toString();
        }

        private long gamma() {
            return Integer.parseInt(toBinaryStringMostCommon(), 2);
        }

        private long epsilon() {
            return Integer.parseInt(toBinaryStringLeastCommon(), 2);
        }

        long powerConsumption() {
            return gamma() * epsilon();
        }

        @Override
        public String toString() {
            return "ByteCounterPerPosition{" +
                    "total=" + totalCount + ", " +
                    "counters=" + Arrays.toString(counters) +
                    '}';
        }

        private String mostCommonPrefix() {
            return toBinaryStringMostCommon().substring(0, 1);
        }

        private String leastCommonPrefix() {
            return toBinaryStringLeastCommon().substring(0, 1);
        }
    }
}
