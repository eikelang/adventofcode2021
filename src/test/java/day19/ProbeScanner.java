package day19;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import static java.util.Collections.singletonList;

class ProbeScanner {

    private static final int MIN_MATCHES = 12;
    private int x;
    private int y;
    private int z;

    private final List<RelativePosition> detectedProbes = new ArrayList<>();

    public void addBeaconPosition(final RelativePosition pos) {
        detectedProbes.add(pos);
    }

    public Map<RelativePosition, Set<RelativePosition>> positionsBasedOnOrigins() {
        return detectedProbes.stream().collect(Collectors.toMap(
                Function.identity(),
                localOrigin -> detectedProbes.stream().map(probe -> probe.relativeTo(localOrigin))
                        .filter(p -> !p.equals(new RelativePosition(0, 0, 0)))
                        .collect(Collectors.toSet())
        ));
    }

    public Map<RelativePosition, Set<Long>> squaredDistancesFromPoint() {
        return detectedProbes.stream().collect(Collectors.toMap(
                Function.identity(),
                localOrigin -> detectedProbes.stream().map(probe -> probe.relativeTo(localOrigin).squaredDistance())
                        .filter(dist -> dist != 0L).collect(Collectors.toSet()))
        );
    }

    public ProbeScanner rotatedBy(final int xSteps, final int ySteps, final int zSteps) {
        final var rotated = new ProbeScanner();
        detectedProbes.stream().map(p -> p.rotateBy(xSteps, ySteps, zSteps))
                .forEach(rotated::addBeaconPosition);
        rotated.x = xSteps;
        rotated.y = ySteps;
        rotated.z = zSteps;
        return rotated;
    }

    List<ProbeScanner> allPermutations() {
        final List<ProbeScanner> permutations = new ArrayList<>();
        final var consecutiveUpTo033 = IntStream.rangeClosed(0, 3).boxed()
                .flatMap(y -> IntStream.rangeClosed(0, 3)
                        .mapToObj(z -> this.rotatedBy(0, y, z)));
        final var remainingElements = IntStream.of(0, 2).boxed().flatMap(y -> IntStream.rangeClosed(0, 3)
                .mapToObj(z -> this.rotatedBy(1, y, z)));
        return Stream.concat(consecutiveUpTo033, remainingElements).toList();
    }

    boolean intersectsWith(final ProbeScanner otherScanner) {
        for (final var scannerOrientation : otherScanner.allPermutations()) {
            final var innerPosition = scannerOrientation.positionsBasedOnOrigins();
            for (final Map.Entry<RelativePosition, Set<RelativePosition>> thisPositionEntry :
                    positionsBasedOnOrigins().entrySet()) {
                for (final Map.Entry<RelativePosition, Set<RelativePosition>> innerPositionEntry :
                        innerPosition.entrySet()) {
                    final var currentSet = new HashSet<>(thisPositionEntry.getValue());
                    currentSet.retainAll(innerPositionEntry.getValue());
                    if (currentSet.size() >= 12) {
                        System.out.println(
                                this + " matches " + otherScanner + " oriented as " + scannerOrientation);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean intersectsOnX(final ProbeScanner otherScanner) {
        return intersectsOnX(otherScanner, MIN_MATCHES);
    }

    Map<Set<Integer>, List<Tuple2<RelativePosition, RelativePosition>>> signatures() {
        final var signatureMap = new HashMap<Set<Integer>, List<Tuple2<RelativePosition, RelativePosition>>>();
        detectedProbes.forEach(probe -> detectedProbes.forEach(innerProbe -> {
            if (probe != innerProbe) {
                signatureMap.merge(probe.pairSignature(innerProbe),
                        new ArrayList<>(singletonList(Tuple.of(innerProbe, probe))),
                        (existingTuples, newTupleSingleton) -> Stream.concat(existingTuples.stream(),
                                newTupleSingleton.stream()).toList());
            }
        }));
        return signatureMap;
    }

    boolean intersectsOnX(final ProbeScanner otherScanner, final int minMatches) {
        final var theseXCoordinates = detectedProbes.stream().map(RelativePosition::xComponent).sorted().toList();
        final var otherXCoordinates =
                otherScanner.detectedProbes.stream().map(RelativePosition::xComponent).sorted().toList();
        // overlay ranges and slide against each other
        return matchesSubrange(theseXCoordinates, otherXCoordinates, minMatches)
                || matchesSubrange(otherXCoordinates, theseXCoordinates, minMatches);
    }

    private boolean matchesSubrange(final List<Integer> referenceRange, final List<Integer> shiftingRange,
            final int minMatches) {
        for (var offset = 0; shiftingRange.subList(offset, shiftingRange.size()).size() >= minMatches; offset++) {
            final var otherSublist = shiftingRange.subList(offset, shiftingRange.size());
            final var shiftAmount = referenceRange.get(0) - otherSublist.get(0);
            final var shiftedOther = otherSublist.stream().map(x -> x + shiftAmount).toList();
            if (haveCommonPrefixOfMinLength(minMatches, referenceRange, shiftedOther)) {
                return true;
            }
        }
        return false;
    }

    private boolean haveCommonPrefixOfMinLength(final int minMatches, final List<Integer> firstList,
            final List<Integer> secondList) {
        var matchCount = 0;
        for (int i = 0; i < Math.min(firstList.size(), secondList.size()); i++) {
            if (Objects.equals(secondList.get(i), firstList.get(i))) {
                matchCount++;
                if (matchCount >= minMatches) {
                    return true;
                }
            } else {
                break;
            }
        }
        return false;
    }

    boolean intersectsWithBasedOnDistances(final ProbeScanner otherScanner) {
        final var innerPosition = otherScanner.squaredDistancesFromPoint();
        for (final Map.Entry<RelativePosition, Set<Long>> thisPositionEntry :
                squaredDistancesFromPoint().entrySet()) {
            for (final Map.Entry<RelativePosition, Set<Long>> otherPositionEntry :
                    innerPosition.entrySet()) {
                final var distancesToCurrentPoint = new HashSet<>(thisPositionEntry.getValue());
                distancesToCurrentPoint.retainAll(otherPositionEntry.getValue());
                if (distancesToCurrentPoint.size() >= 12) {
                    System.out.println(
                            this + " matches " + otherScanner + " by distance on local point "
                                    + thisPositionEntry.getKey() + " and other point "
                                    + otherPositionEntry.getKey());
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "ProbeScanner{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public boolean overlaps(final ProbeScanner scanner1) {
        return false;
    }

    public List<RelativePosition> getDetectedProbes() {
        return detectedProbes;
    }
}
