package day16;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

enum ParsingStrategy {
    LENGTH_BASED {
        @Override
        List<Packet> parse(final StatefulParsing binaryInput) {
            final StatefulParsing toParse = binaryInput.getSubparseByIncludedLength();
            final List<Packet> parsedPackets = new ArrayList<>();
            while (!toParse.isEmpty()) {
                parsedPackets.add(new Packet(toParse));
            }
            return parsedPackets;

        }
    },
    COUNT_BASED {
        @Override
        List<Packet> parse(final StatefulParsing binaryInput) {
            final var subpacketsCount = binaryInput.parseIntOfNBits(11);
            return IntStream.range(0, subpacketsCount)
                    .mapToObj(i -> new Packet(binaryInput))
                    .toList();

        }
    };

    abstract List<Packet> parse(final StatefulParsing binaryInput);

    public static ParsingStrategy forLengthType(final String lengthType) {
        return "0".equals(lengthType) ? LENGTH_BASED : COUNT_BASED;
    }
}
