import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day7 {

    private List<Long> readFile() throws IOException {
        var inputStream = Day7.class.getResourceAsStream("/day7.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        var scanner = new Scanner(buf.readLine());
        scanner.useDelimiter(",");
        return scanner.tokens()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    @Test
    void optimalDepth() throws IOException {
        long fuelConsumption = fuelConsumptionWithCostFunction(this::linearCostFunction, readFile());
        assertThat(fuelConsumption).isEqualTo(345197);
    }

    @Test
    void optimalDepthNonLinear() throws IOException {
        long fuelConsumption = fuelConsumptionWithCostFunction(this::increasingCostFunction, readFile());
        assertThat(fuelConsumption).isEqualTo(96361606);
    }

    @Test
    void increasingCostFunction() {
        assertThat(increasingCostFunction(5).applyAsLong(16L)).isEqualTo(66);
    }

    @Test
    void increasingCostFunction2() {
        assertThat(increasingCostFunction(5).applyAsLong(1L)).isEqualTo(10);
    }

    @Test
    void increasingCostFunction3() {
        assertThat(increasingCostFunction(5).applyAsLong(2L)).isEqualTo(6);
    }

    @Test
    void increasingCostFunction4() {
        assertThat(increasingCostFunction(5).applyAsLong(4L)).isEqualTo(1);
    }

    @Test
    void increasingCostFunction5() {
        assertThat(increasingCostFunction(5).applyAsLong(14L)).isEqualTo(45);
    }

    @Test
    void fullIncreasingExample() {
        final List<Long> positions = LongStream.of(16,1,2,0,4,2,7,1,2,14).boxed().collect(Collectors.toList());
        assertThat(fuelConsumptionWithCostFunction(this::increasingCostFunction, positions)).isEqualTo(168);
    }

    private long fuelConsumptionWithCostFunction(final Function<Long, ToLongFunction<Long>> costFunction,
            final List<Long> positions) {
        final LongSummaryStatistics stats = positions.stream().mapToLong(i -> i).summaryStatistics();
        final var minDepth = stats.getMin();
        final var maxDepth = stats.getMax();
        var fuelConsumption = Long.MAX_VALUE;

        for (long targetDepth = minDepth; targetDepth<= maxDepth; targetDepth++) {
            final long fuelConsumptionForCycle = positions.stream()
                    .mapToLong(costFunction.apply(targetDepth))
                    .sum();

            if (fuelConsumption > fuelConsumptionForCycle) {
                fuelConsumption = fuelConsumptionForCycle;
            }
        }
        return fuelConsumption;
    }

    private ToLongFunction<Long> linearCostFunction(final long targetDepth) {
        return depth -> Math.abs(depth - targetDepth);
    }

    private ToLongFunction<Long> increasingCostFunction(final long targetDepth) {
        return depth -> {
            final var steps = Math.abs(depth - targetDepth);
            return LongStream.rangeClosed(0, steps).sum();
        };
    };
}
