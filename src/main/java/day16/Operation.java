package day16;

import java.util.stream.Stream;

public interface Operation {
    long apply(final Stream<Packet> operands);

    static Operation returningConstantValue(final long value) {
        return new Operation() {

            @Override
            public long apply(final Stream<Packet> operands) {
                return value;
            }

            @Override
            public String toString() {
                return Long.toString(value);
            }
        };
    }
}
