import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day2 {

    private Stream<String> readFile() {
        var inputStream = Day2.class.getResourceAsStream("/day2.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    @Test
    void moveIt() {
        final var submarine = new SimpleSubmarine();
        readFile().forEach(submarine::parse);

        assertThat(submarine.positionByDepth()).isEqualTo(1670340);
    }

    @Test
    void moveItAimed() {
        final var submarine = new AimedSubmarine();
        readFile().forEach(submarine::parse);

        assertThat(submarine.positionByDepth()).isEqualTo(1954293920);
    }

    private interface Submarine {

        void up(final int steps);

        void down(final int steps);

        void forward(final int steps);

        int positionByDepth();

        default void parse(final String line) {
            final var parts = line.split(" ");
            final var command = parts[0];
            final var steps = Integer.parseInt(parts[1]);
            switch (command) {
            case "up":
                up(steps);
                break;
            case "down":
                down(steps);
                break;
            case "forward":
                forward(steps);
                break;
            }
        }

    }

    private static class SimpleSubmarine implements Submarine {

        private int depth;
        private int pos;

        @Override
        public void up(final int steps) {
            depth -= steps;
        }

        @Override
        public void down(final int steps) {
            depth += steps;
        }

        @Override
        public void forward(final int steps) {
            pos += steps;
        }

        @Override
        public int positionByDepth() {
            return pos * depth;
        }
    }

    private static class AimedSubmarine implements Submarine {

        private int depth;
        private int pos;
        private int aim;

        @Override
        public void up(final int steps) {
            aim -= steps;
        }

        @Override
        public void down(final int steps) {
            aim += steps;
        }

        @Override
        public void forward(final int steps) {
            pos += steps;
            depth += aim * steps;
        }

        @Override
        public int positionByDepth() {
            return pos * depth;
        }
    }
}
