package day16;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Packet {

    static final int TYPEID_LITERAL = 4;
    public static final int AFTER_HEADER = 6;

    private final int version;
    private final Operation operation;
    private final List<Packet> subPackets;

    Packet(final StatefulParsing binaryData) {
        version = binaryData.parseVersion();
        final int parsedTypeId = binaryData.parseTypeId();
        if (parsedTypeId == 4) {
            subPackets = Collections.emptyList();
            final long value = binaryData.parseLiteralValue();
            operation = Operation.returningConstantValue(value);
        } else {
            final ParsingStrategy parsingStrategy = ParsingStrategy.forLengthType(binaryData.parseSingleBit());
            subPackets = parsingStrategy.parse(binaryData);
            operation = SubpacketsOperation.fromTypeId(parsedTypeId);
        }
    }

    Stream<Packet> subPackets() {
        return subPackets.stream();
    }

    int versionSum() {
        return version + subPackets().mapToInt(Packet::versionSum).sum();
    }

    long value() {
        return operation.apply(subPackets());
    }

    @Override
    public String toString() {
        return operation + subPackets().map(Packet::toString)
                .collect(Collectors.joining(",", "(", ")"));
    }

    int version() {
        return version;
    }
}
