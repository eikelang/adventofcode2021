package day11;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class JellyfishField {

    private final Map<Coordinate, JellyFishState> jellyFishField;
    private final int maxXindex;
    private final int maxYindex;

    private JellyfishField(final Map<Coordinate, JellyFishState> field) {
        this.jellyFishField = field;
        this.maxXindex = jellyFishField.keySet().stream().mapToInt(coord -> coord.x).max().orElse(0);
        this.maxYindex = jellyFishField.keySet().stream().mapToInt(coord -> coord.y).max().orElse(0);
    }

    static JellyfishField fromInput(final Stream<String> lines) {
        final Map<Coordinate, JellyFishState> field = new HashMap<>();
        final var x = new AtomicInteger(0);
        final var y = new AtomicInteger(0);
        lines.forEach(line -> {
            for (final String s : line.split("")) {
                var counter = 0;
                field.put(new Coordinate(counter, y.intValue()), new JellyFishState(Integer.parseInt(s)));
                counter++;
                if (x.intValue() == 0) {
                    x.set(counter);
                }
            }
            y.incrementAndGet();
        });
        return new JellyfishField(field);
    }

//    long step() {
//        jellyFishField.values().forEach(JellyFishState::incrementEnergy);
//        while (jellyFishField.values().stream().anyMatch(JellyFishState::willFlash)) {
//            jellyFishField.entrySet().stream().filter(e -> e.getValue().willFlash())
//                    .peek(e -> e.getValue().flash())
//                    .flatMap(e -> e.getKey().allNeighbours().map(jellyFishField::get))
//                    .forEach(JellyFishState::incrementEnergy);
//        }
//        final var flashes = jellyFishField.values().stream().filter(JellyFishState::didFlash).count();
//        jellyFishField.values().stream().filter(JellyFishState::didFlash).forEach(JellyFishState::startNextCycle);
//        return flashes;
//    }

    private static class JellyFishState {

        private int energyCounter;
        private boolean didFlash;

        private JellyFishState(final int initialEnergy) {
            this.energyCounter = initialEnergy;
        }

        void incrementEnergy() {
            energyCounter++;
        }

        boolean willFlash() {
            return energyCounter > 9 && didFlash();
        }

        void startNextCycle() {
            energyCounter = 0;
            this.didFlash = false;
        }

        void flash() {
            this.didFlash = true;
        }

        boolean didFlash() {
            return didFlash;
        }

    }
}
