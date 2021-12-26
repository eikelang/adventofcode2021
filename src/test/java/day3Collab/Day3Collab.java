package day3Collab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class Day3Collab {

    public static final String SAMPLE_INPUT = """
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

    @Test
    void acceptanceTestGammaBinary() {
        assertThat(binaryGamma(sampleAsList())).isEqualTo("10110");
    }

    private List<String> sampleAsList() {
        return SAMPLE_INPUT.lines().toList();
    }

    @Test
    void acceptanceTestEpsilonBinary() {
        assertThat(binaryEpsilon(sampleAsList())).isEqualTo("01001");
    }

    @Test
    void acceptancePowerConsumption() {
        assertThat(powerConsumption(sampleAsList())).isEqualTo(198L);
    }

    @Test
    void powerConsumptionForPuzzleInput() {
        assertThat(powerConsumption(readFile())).isEqualTo(4191876L);
    }

    @Test
    void oxygenGeneratorRatingForSample() {
        assertThat(oxygenRating(sampleAsList())).isEqualTo("10111");
    }

    @Test
    void scrubberRatingForSample() {
        assertThat(scrubberRating(sampleAsList())).isEqualTo("01010");
    }

    @Test
    void lifeSupportRatingForSample() {
        assertThat(lifeSupportRating(sampleAsList())).isEqualTo(230);
    }

    @Test
    void lifeSupportRatingForPuzzleInput() {
        assertThat(lifeSupportRating(readFile())).isEqualTo(3414905L);
    }

    @Test
    void transposeBitLines() {
        final var line1 = asList(true, true, true);
        final var line2 = asList(false, true, true);

        final var input = asList(line1, line2);

        assertThat(transpose(input)).containsExactly(asList(true, false), asList(true, true), asList(true, true));
    }

    private long lifeSupportRating(final List<String> input) {
        final var oxgenRating = oxygenRating(input);
        final var scrubberRating = scrubberRating(input);
        return Integer.parseInt(oxgenRating, 2) * Integer.parseInt(scrubberRating, 2);
    }

    private String scrubberRating(final List<String> input) {
        return genericRating(input, (count, total) -> count < total - count);
    }

    private String oxygenRating(final List<String> input) {
        return genericRating(input, (count, total) -> 2 * count >= total);
    }

    private String genericRating(final List<String> input,
            final BiFunction<Long, Long, Boolean> shouldProceedWithOnes) {
        if (input.stream().allMatch(String::isEmpty)) {
            return "";
        }
        if (input.size() == 1) {
            return input.get(0);
        }
        final long totalEntries = input.size();
        var prefix = "";
        final var totalOnes = input.stream().map(s -> s.substring(0, 1))
                .filter("1"::equals).count();
        if (shouldProceedWithOnes.apply(totalOnes, totalEntries)) {
            return prefix + "1" + genericRating(subListWithPrefix(input, "1"), shouldProceedWithOnes);
        } else {
            return prefix + "0" + genericRating(subListWithPrefix(input, "0"), shouldProceedWithOnes);
        }
    }

    private List<List<Boolean>> toChars(final List<String> input) {
        return input.stream().map(s -> s.chars().mapToObj(c -> c == '1').toList()).toList();
    }

    private List<List<Boolean>> transpose(final List<List<Boolean>> input) {
        return IntStream.range(0, input.get(0).size())
                .mapToObj(currentIndex -> input.stream()
                        .flatMap(s -> s.subList(currentIndex, currentIndex + 1).stream()).toList())
                .toList();
    }

    private List<String> subListWithPrefix(final List<String> input, final String prefix) {
        return input.stream()
                .filter(s -> s.startsWith(prefix))
                .map(s -> s.substring(1)).toList();
    }

    private String binaryEpsilon(final List<String> lines) {
        return flipBits(binaryGamma(lines));
    }

    private String flipBits(final String input) {
        return input.replace('1', 'x')
                .replace('0', '1')
                .replace('x', '0');
    }

    private long powerConsumption(final List<String> lines) {
        final var gamma = binaryGamma(lines);
        final var epsilon = binaryEpsilon(lines);
        return Integer.parseInt(gamma, 2) * Integer.parseInt(epsilon, 2);
    }

    private String binaryGamma(final List<String> lines) {
        final var entryWidth = lines.get(0).length();

        final var onesAtPosition = new int[entryWidth];

        for (final String inputLine : lines) {
            for (int i = 0; i < entryWidth; i++) {
                if (inputLine.charAt(i) == '1') {
                    onesAtPosition[i]++;
                }
            }
        }

        final var totalEntries = lines.size();
        return Arrays.stream(onesAtPosition)
                .mapToObj(counter -> 2 * counter >= totalEntries ? "1" : "0")
                .collect(Collectors.joining());
    }

    private List<String> readFile() {
        var inputStream = Day3Collab.class.getResourceAsStream("/day3.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines().toList();
    }
/*
                     1     10   101   1011   10111
            00100
            11110    1110
            10110    0110  110  10    0
            10111    0111  111  11    1
            10101    0101  101  01
            01111
            00111
            11100    1100
            10000    0000  000
            11001    1001
            00010
            01010

            74875    3533 3     2


            00100
            01111  01111
            00111
            00010
            01010  01010   01010

            0      1       0
    Start again with all 12 numbers and consider only the first bit of each number. There are fewer 0 bits (5) than 1
     bits (7), so keep only the 5 numbers with a 0 in the first position: 00100, 01111, 00111, 00010, and 01010.
    Then, consider the second bit of the 5 remaining numbers: there are fewer 1 bits (2) than 0 bits (3), so keep
    only the 2 numbers with a 1 in the second position: 01111 and 01010.
    In the third position, there are an equal number of 0 bits and 1 bits (one each). So, to find the CO2 scrubber
    rating, keep the number with a 0 in that position: 01010.
    As there is only one number left, stop; the CO2 scrubber rating is 01010, or 10 in decimal.


 */

}
