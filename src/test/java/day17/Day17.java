package day17;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import common.Coordinate;

import static org.assertj.core.api.Assertions.assertThat;

class Day17 {

    private static final String TARGET = "target area: x=96..125, y=-144..-98\n";
    private static final int TARGET_X_MIN = 96;
    private static final int TARGET_X_MAX = 125;
    private static final int TARGET_Y_MAX = -98;
    private static final int TARGET_Y_MIN = -144;
    private static final int X_SPREAD = TARGET_X_MAX - TARGET_X_MIN;
    private static final int Y_SPREAD = TARGET_Y_MAX - TARGET_Y_MIN;

    @Test
    void tryVelocities() {
        final List<Integer> xCandidates = IntStream.range(0, TARGET_X_MAX)
                .mapToObj(i -> new Probe(i, 0))
                .filter(probe2 -> probe2.tryLaunchX(TARGET_X_MIN, TARGET_X_MAX))
                .map(Probe::initialXVelocity)
                .toList();
        final List<Integer> yCandidates = IntStream.range(0, Math.abs(TARGET_Y_MIN))
                .mapToObj(i -> new Probe(0, i))
                .filter(probe1 -> probe1.tryLaunchY(TARGET_Y_MIN, TARGET_Y_MAX))
                .map(Probe::initialYVelocity)
                .toList();
        final Optional<Probe> highestFlyingProbe = xCandidates.stream()
                .flatMap(x -> yCandidates.stream().map(y -> new Probe(x, y)))
                .filter(probe1 -> probe1.tryLaunch(TARGET_X_MIN, TARGET_X_MAX, TARGET_Y_MIN, TARGET_Y_MAX))
                .max(Comparator.comparing(Probe::initialYVelocity));

        final var reprobe = highestFlyingProbe
                .map(p -> new Probe(p.initialXVelocity(), p.initialYVelocity())).orElseThrow();
        int maxY = 0;
        while (!reprobe.inTargetArea(TARGET_X_MIN, TARGET_X_MAX, TARGET_Y_MIN, TARGET_Y_MAX)) {
            System.out.println(reprobe.position());
            reprobe.step();
            maxY = Math.max(maxY, reprobe.position().yValue());
        }
        assertThat(maxY).isEqualTo(10296);
    }

    @Test
    void tryExample() {
        final int targetXmin = 20;
        final int targetXmax = 30;
        final int targetYmin = -10;
        final int targetYmax = -5;

        final Probe probe = new Probe(7, 2);
        while (!probe.targetAreaUnreachable(TARGET_X_MAX, TARGET_Y_MIN, TARGET_X_MIN) || probe.inTargetArea(targetXmin,
                targetXmax, targetYmin, targetYmax)) {
            System.out.println("Position: " + probe.position());
            probe.step();
        }

    }

    private class Probe {
        private final int initialYVelocity;
        private final int initialXVelocity;
        private int xVel;
        private int yVel;
        private int xPos;
        private int yPos;

        public Probe(final int initialXVelocity, final int initialYVelocity) {
            this.xVel = initialXVelocity;
            this.yVel = initialYVelocity;
            this.initialYVelocity = initialYVelocity;
            this.initialXVelocity = initialXVelocity;
        }

        int initialXVelocity() {
            return initialXVelocity;
        }

        int initialYVelocity() {
            return initialYVelocity;
        }

        void step() {
            this.xPos += xVel;
            this.yPos += yVel;
            if (xVel > 0) {
                xVel -= 1;
            }
            yVel -= 1;
        }

        boolean tryLaunchX(final int targetXMin, final int targetXMax) {
            while (!isWithinXRange(targetXMin, targetXMax) && xVel > 0) {
                step();
            }
            return isWithinXRange(targetXMin, targetXMax);
        }

        boolean tryLaunchY(final int targetYMin, final int targetYMax) {
            while (!isWithinYRange(targetYMin, targetYMax) && yPos > targetYMin) {
                step();
            }
            return isWithinYRange(targetYMin, targetYMax);
        }

        boolean tryLaunch(final int targetXMin, final int targetXMax, final int targetYMin, final int targetYMax) {
            while (!inTargetArea(targetXMin, targetXMax, targetYMin, targetYMax) && !targetAreaUnreachable(TARGET_X_MAX,
                    TARGET_Y_MIN, TARGET_X_MIN)) {
                step();
            }
            return inTargetArea(targetXMin, targetXMax, targetYMin, targetYMax);
        }

        boolean inTargetArea(final int targetXMin, final int targetXMax, final int targetYMin, final int targetYMax) {
            final boolean withinXRange = isWithinXRange(targetXMin, targetXMax);
            final boolean withinYRange = isWithinYRange(targetYMin, targetYMax);
            return withinXRange && withinYRange;
        }

        boolean isWithinYRange(final int targetYMin, final int targetYMax) {
            return targetYMin <= yPos && yPos <= targetYMax;
        }

        boolean isWithinXRange(final int targetXMin, final int targetXMax) {
            return targetXMin <= xPos && xPos <= targetXMax;
        }

        boolean targetAreaUnreachable(final int targetXMax, final int targetYMin, final int targetXMin) {
            return overShotOnX(targetXMax) || overShotOnY(targetYMin) || stalledOnX(targetXMin);
        }

        private boolean stalledOnX(final int targetXMin) {
            return xPos < targetXMin && xVel == 0;
        }

        private boolean overShotOnY(final int yMin) {
            return yPos < yMin;
        }

        private boolean overShotOnX(final int targetXMax) {
            return xPos > targetXMax;
        }

        Coordinate position() {
            return new Coordinate(xPos, yPos);
        }
    }
}
