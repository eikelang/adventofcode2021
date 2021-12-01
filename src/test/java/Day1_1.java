import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Day1_1 {

    private IntStream readFile() {
        var inputStream = Day1_1.class.getResourceAsStream("/day1_1.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines().mapToInt(Integer::parseInt);
    }

    @Test
    void calculateDrops() {
        final Optional<DepthWithCounter> depthDrops = readFile().mapToObj(DepthWithCounter::new)
                .reduce(DepthWithCounter::accumulateDepth);
        assertThat(depthDrops).map(d -> d.counter).contains(1722);
    }

    @Test
    void slidingWindow() {
        final List<Integer> measurements = readFile().boxed().collect(Collectors.toList());
        final Optional<DepthWithCounter> dropsCount = IntStream.range(0, measurements.size())
                .map(i -> windowSum(i, measurements))
                .mapToObj(DepthWithCounter::new)
                .reduce(DepthWithCounter::accumulateDepth);
        assertThat(dropsCount).map(d -> d.counter).contains(1748);
    }

    @Test
    void slidingWindowCalculation() {
        assertThat(windowSum(0, Arrays.asList(1,2,3))).isEqualTo(6);
    }

    @Test
    void slidingWindowCalculatio2() {
        assertThat(windowSum(1, Arrays.asList(1,2,3,4))).isEqualTo(9);
    }


    private int windowSum(int index, List<Integer> values) {
        if (index < values.size() - 2) {
            return values.stream().skip(index).limit(3).mapToInt(i -> i).sum();
        }
        return 0;
    }

    private static class DepthWithCounter {

        private final int depth;
        private int counter;

        private DepthWithCounter(final int depth) {
            this(depth, 0);
        }

        private DepthWithCounter(final int depth, final int counter) {
            this.depth = depth;
            this.counter = counter;
        }

        private DepthWithCounter accumulateDepth(final DepthWithCounter d2) {
            return new DepthWithCounter(d2.depth, d2.depth > depth ? counter + 1 : counter);
        }
    }
}
