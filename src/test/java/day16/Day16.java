package day16;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day16 {

    private static final String SAMPLE_PACKET = "D2FE28";
    public static final int AFTER_HEADER = 6;
    public static final int TYPEID_LITERAL = 4;

    private String readFile() throws IOException {
        var inputStream = Day16.class.getResourceAsStream("/day16.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.readLine();
    }

    @Test
    void getVersionAndTypeInfoFromSample() {
        final Packet packet = new Packet(SAMPLE_PACKET);
        assertThat(packet.version).isEqualTo(6);
        assertThat(packet.typeId).isEqualTo(TYPEID_LITERAL);
    }

    @Test
    void getValueFromLiteral() {
        final Packet packet = new Packet(SAMPLE_PACKET);
        assertThat(packet.value()).isEqualTo(2021);
    }

    @Test
    void getVersionAndTypeFromOperatorPacket() {
        final Packet packet = new Packet("38006F45291200");
        assertThat(packet.version).isEqualTo(1);
        assertThat(packet.typeId).isEqualTo(6);
    }

    @Test
    void getSubpacketsFromOperatorPacket() {
        final Packet packet = new Packet("38006F45291200");
        assertThat(packet.subpackets()).extracting(Packet::value)
                .containsExactly(10L, 20L);
    }

    @Test
    void versionSum1() {
        final Packet packet = new Packet("8A004A801A8002F478");
        final var versionSum = packet.versionSum();
        assertThat(versionSum).isEqualTo(16);
    }

    @Test
    void versionSum2() {
        final Packet packet = new Packet("620080001611562C8802118E34");
        final var versionSum = packet.versionSum();
        assertThat(versionSum).isEqualTo(12);
    }

    @Test
    void versionSum3() {
        final Packet packet = new Packet("C0015000016115A2E0802F182340");
        final var versionSum = packet.versionSum();
        assertThat(versionSum).isEqualTo(23);
    }

    @Test
    void versionSum4() {
        final Packet packet = new Packet("A0016C880162017C3686B18A3D4780");
        final var versionSum = packet.versionSum();
        assertThat(versionSum).isEqualTo(31);
    }

    @Test
    void versionSumFile() throws IOException {
        final Packet packet = new Packet(readFile());
        final var versionSum = packet.versionSum();
        assertThat(versionSum).isEqualTo(821L);
    }

    @Test
    void valueFile() throws IOException {
        final Packet packet = new Packet(readFile());
        System.out.println(packet);
        assertThat(packet.value()).isEqualTo(2056021084691L);
    }

    @Test
    void parseTwoLiteralPacketsFromStream() {
        final OngoingParsing ongoingParsing =
                new OngoingParsing(Arrays.stream("110100010100101001000100100".split("")).toList());
        ongoingParsing.parse();
        final Stream<Packet> packetStream = ongoingParsing.packets();
        assertThat(packetStream).extracting(Packet::value).containsExactly(10L, 20L);
    }

    private static class Packet {

        private List<String> rawData;
        private int version;
        private int typeId;
        private final String lengthType;
        private List<String> remainingInputAsBinaryDigits;
        private long value;
        private List<Packet> subPackets;

        Packet(final String input) {
            this(hexToBinary(input));
        }

        Packet(final List<String> binaryData) {
            version = parseVersion(binaryData);
            typeId = parseTypeId(binaryData);
            if (!isLiteral()) {
                rawData = binaryData.subList(AFTER_HEADER + 1, binaryData.size());
                lengthType = binaryData.get(AFTER_HEADER);
                subPackets = computeSubpackets().toList();
            } else {
                rawData = binaryData.subList(AFTER_HEADER, binaryData.size());
                lengthType = null;
                computeValue();
            }
        }

        private static List<String> hexToBinary(final String input) {
            return input.chars().boxed()
                    .map(c -> Integer.parseInt(Character.toString(c), 16))
                    .map(c -> Integer.toString(c, 2))
                    .map(Day16::padToFour)
                    .flatMap(s -> Arrays.stream(s.split("")))
                    .toList();
        }

        private int parseTypeId(final List<String> binaryData) {
            return Integer.parseInt(binaryData.stream().skip(3).limit(3).collect(Collectors.joining()), 2);
        }

        private int parseVersion(final List<String> binaryData) {
            return Integer.parseInt(binaryData.stream().limit(3).collect(Collectors.joining()), 2);
        }

        boolean isLiteral() {
            return typeId == TYPEID_LITERAL;
        }

        private void computeValue() {
            StringBuilder bitAccumulator = new StringBuilder();
            boolean keepReading = true;
            var offset = 0;
            for (; keepReading; offset += 5) {
                final var chunk = rawData.subList(offset, offset + 5);
                bitAccumulator.append(chunk.stream().skip(1).collect(Collectors.joining("")));
                keepReading = "1".equals(chunk.get(0));
            }
            remainingInputAsBinaryDigits = rawData.subList(offset, rawData.size());
            this.value = Long.parseLong(bitAccumulator.substring(0, bitAccumulator.length()), 2);
        }

        public Stream<Packet> subpackets() {
            return subPackets.stream();
        }

        private Stream<Packet> computeSubpackets() {
            if (isLiteral()) {
                return Stream.empty();
            }
            if ("0".equals(lengthType)) {
                final var subpacketsLength =
                        Integer.parseInt(String.join("", rawData.subList(0, 15)), 2);
                final OngoingParsing ongoingParsing = new OngoingParsing(rawData.subList(15, 15 + subpacketsLength));
                ongoingParsing.parse();
                remainingInputAsBinaryDigits = rawData.subList(15 + subpacketsLength, rawData.size());
                return ongoingParsing.packets();
            } else {
                final var subpacketsCount =
                        Integer.parseInt(String.join("", rawData.subList(0, 11)), 2);
                final OngoingParsing ongoingParsing = new OngoingParsing(rawData.subList(11, rawData.size()));
                remainingInputAsBinaryDigits = ongoingParsing.parseCounted(subpacketsCount);
                return ongoingParsing.packets();
            }
        }

        public int versionSum() {
            if (isLiteral()) {
                return version;
            }
            return version + subpackets().mapToInt(Packet::versionSum).sum();
        }

        public static Stream<Packet> parseStream(final String bitstream) {
            return null;
        }

        public List<String> remainingInput() {
            return Collections.unmodifiableList(remainingInputAsBinaryDigits);
        }

        public long value() {
            if (isLiteral()) {
                return value;
            }
            final var operation = Operation.fromTypeId(typeId);
            return operation.apply(subpackets());
        }

        @Override
        public String toString() {
            if (isLiteral()) {
                return String.valueOf(value());
            } else {
                final var operation = Operation.fromTypeId(typeId);
                return operation + "(" + subpackets().map(Packet::toString)
                        .collect(Collectors.joining(",")) + ")";
            }
        }
    }

    private enum Operation {
        SUM {
            @Override
            long apply(final Stream<Packet> operands) {
                return operands.mapToLong(Packet::value).sum();
            }
        }, PRODUCT {
            @Override
            long apply(final Stream<Packet> operands) {
                return operands.mapToLong(Packet::value).reduce(1L, (l1, l2) -> l1 * l2);
            }
        }, MIN {
            @Override
            long apply(final Stream<Packet> operands) {
                return operands.mapToLong(Packet::value).min().orElse(0L);
            }
        }, MAX {
            @Override
            long apply(final Stream<Packet> operands) {
                return operands.mapToLong(Packet::value).max().orElse(0L);
            }
        }, GT {
            @Override
            long apply(final Stream<Packet> operands) {
                return operands.mapToLong(Packet::value).reduce((l1, l2) -> l1 < l2 ? 1 : 0L).orElse(0L);
            }
        }, LT {
            @Override
            long apply(final Stream<Packet> operands) {
                return operands.mapToLong(Packet::value).reduce((l1, l2) -> l1 > l2 ? 1 : 0L).orElse(0L);
            }
        }, EQ {
            @Override
            long apply(final Stream<Packet> operands) {
                return operands.mapToLong(Packet::value).reduce((l1, l2) -> l1 == l2 ? 1 : 0L).orElse(0L);
            }
        };

        abstract long apply(final Stream<Packet> operands);

        public static Operation fromTypeId(final int typeId) {
            switch (typeId) {
            case 0:
                return SUM;
            case 1:
                return PRODUCT;
            case 2:
                return MIN;
            case 3:
                return MAX;
            case 5:
                return LT;
            case 6:
                return GT;
            case 7:
                return EQ;
            default:
                throw new RuntimeException();
            }
        }
    }

    private static class OngoingParsing {

        List<Packet> parsedPackets = new ArrayList<>();
        List<String> unconsumedInput;

        private OngoingParsing(final List<String> unconsumedInput) {
            this.unconsumedInput = unconsumedInput;
        }

        void parse() {
            var hasInput = true;
            do {
                final var packet = new Packet(unconsumedInput);
                parsedPackets.add(packet);
                unconsumedInput = packet.remainingInput();
            } while (!unconsumedInput.isEmpty());
        }

        List<String> parseCounted(int numberOfPackets) {
            for (int i = 0; i < numberOfPackets; i++) {
                final var packet = new Packet(unconsumedInput);
                parsedPackets.add(packet);
                unconsumedInput = packet.remainingInput();
            }
            return unconsumedInput;
        }

        Stream<Packet> packets() {
            return parsedPackets.stream();
        }
    }

    private static String padToFour(final String input) {
        final var toPad = TYPEID_LITERAL - input.length();
        return "0".repeat(toPad) + input;
    }
}
