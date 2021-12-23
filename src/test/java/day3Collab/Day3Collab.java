package day3Collab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

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
        final var inputLines = lines;
        final var totalEntries = inputLines.size();
        final var entryWidth = inputLines.get(0).length();

        final var onesAtPosition = new Integer[entryWidth];
        for (int i = 0; i < onesAtPosition.length; i++) {
            onesAtPosition[i] = 0;
        }

        for (final String inputLine : inputLines) {
            for (int i = 0; i < entryWidth; i++) {
                if (inputLine.charAt(i) == '1') {
                    onesAtPosition[i]++;
                }
            }
        }

        final var resultBuilder = new StringBuilder();
        for (final int onesCount : onesAtPosition) {
            final var zeroesCount = totalEntries - onesCount;
            if (onesCount > zeroesCount) {
                resultBuilder.append("1");
            } else {
                resultBuilder.append("0");
            }
        }
        return resultBuilder.toString();
    }

    private List<String> readFile() {
        var inputStream = Day3Collab.class.getResourceAsStream("/day3.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines().toList();
    }
}
