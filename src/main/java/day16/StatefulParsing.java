package day16;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class StatefulParsing {
    private final Deque<String> binaryString;

    StatefulParsing(final List<String> binaryString) {
        this.binaryString = new ArrayDeque<>(binaryString);
    }

    long parseLiteralValue() {
        final StringBuilder bitAccumulator = new StringBuilder();
        boolean keepReading = true;
        while (keepReading) {
            final var chunk = takeN(5, binaryString);
            bitAccumulator.append(chunk.substring(1));
            keepReading = '1' == chunk.charAt(0);
        }
        return Long.parseLong(bitAccumulator.toString(), 2);
    }

    int parseTypeId() {
        return Integer.parseInt(takeN(3, binaryString), 2);
    }

    int parseVersion() {
        return Integer.parseInt(takeN(3, binaryString), 2);
    }

    private String takeN(final int n) {
        return takeN(n, binaryString);
    }

    private static String takeN(final int n, final Deque<String> deque) {
        final var stringBuilder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            stringBuilder.append(deque.pop());
        }
        return stringBuilder.toString();
    }

    public boolean isEmpty() {
        return binaryString.isEmpty();
    }

    String parseSingleBit() {
        return binaryString.pop();
    }

    int parseIntOfNBits(final int n) {
        return Integer.parseInt(takeN(n), 2);
    }

    StatefulParsing getSubparseByIncludedLength() {
        final var subpacketsLength = parseIntOfNBits(15);
        final var substream = takeN(subpacketsLength);
        return new StatefulParsing(Arrays.stream(substream.split("")).toList());
    }
}
