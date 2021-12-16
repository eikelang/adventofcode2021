package day16;

import java.util.stream.LongStream;
import java.util.stream.Stream;

enum SubpacketsOperation implements Operation {
    SUM {
        @Override
        public long apply(final Stream<Packet> operands) {
            return inputAsLongs(operands).sum();
        }
    },
    PRODUCT {
        @Override
        public long apply(final Stream<Packet> operands) {
            return inputAsLongs(operands).reduce(1L, (l1, l2) -> l1 * l2);
        }
    },
    MIN {
        @Override
        public long apply(final Stream<Packet> operands) {
            return inputAsLongs(operands).min().orElse(0L);
        }
    },
    MAX {
        @Override
        public long apply(final Stream<Packet> operands) {
            return inputAsLongs(operands).max().orElse(0L);
        }
    },
    GT {
        @Override
        public long apply(final Stream<Packet> operands) {
            return inputAsLongs(operands).reduce((l1, l2) -> l1 < l2 ? 1 : 0L).orElse(0L);
        }
    },
    LT {
        @Override
        public long apply(final Stream<Packet> operands) {
            return inputAsLongs(operands).reduce((l1, l2) -> l1 > l2 ? 1 : 0L).orElse(0L);
        }
    },
    EQ {
        @Override
        public long apply(final Stream<Packet> operands) {
            return inputAsLongs(operands).reduce((l1, l2) -> l1 == l2 ? 1 : 0L).orElse(0L);
        }
    };

    private static LongStream inputAsLongs(final Stream<Packet> operands) {
        return operands.mapToLong(Packet::value);
    }

    public static SubpacketsOperation fromTypeId(final int typeId) {
        return switch (typeId) {
            case 0 -> SUM;
            case 1 -> PRODUCT;
            case 2 -> MIN;
            case 3 -> MAX;
            case 5 -> LT;
            case 6 -> GT;
            case 7 -> EQ;
            default -> throw new RuntimeException();
        };
    }
}
