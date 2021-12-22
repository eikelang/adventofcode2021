package day22;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;

import static org.assertj.core.api.Assertions.assertThat;

public class Day22 {

    private static final String SAMPLE_INSTRUCTIONS = """
            on x=-20..26,y=-36..17,z=-47..7
            on x=-20..33,y=-21..23,z=-26..28
            on x=-22..28,y=-29..23,z=-38..16
            on x=-46..7,y=-6..46,z=-50..-1
            on x=-49..1,y=-3..46,z=-24..28
            on x=2..47,y=-22..22,z=-23..27
            on x=-27..23,y=-28..26,z=-21..29
            on x=-39..5,y=-6..47,z=-3..44
            on x=-30..21,y=-8..43,z=-13..34
            on x=-22..26,y=-27..20,z=-29..19
            off x=-48..-32,y=26..41,z=-47..-37
            on x=-12..35,y=6..50,z=-50..-2
            off x=-48..-32,y=-32..-16,z=-15..-5
            on x=-18..26,y=-33..15,z=-7..46
            off x=-40..-22,y=-38..-28,z=23..41
            on x=-16..35,y=-41..10,z=-47..6
            off x=-32..-23,y=11..30,z=-14..3
            on x=-49..-5,y=-3..45,z=-29..18
            off x=18..30,y=-20..-8,z=-3..13
            on x=-41..9,y=-7..43,z=-33..15
            on x=-54112..-39298,y=-85059..-49293,z=-27449..7877
            on x=967..23432,y=45373..81175,z=27513..53682""";

    @Test
    void switchExample() {
        final var grid = new ReactorGrid();
        SAMPLE_INSTRUCTIONS.lines().limit(20).forEach(line -> {
            final var on = line.startsWith("on");
            final var commands = line.substring(line.indexOf(' ') + 1);
            final var ranges = commands.split(",");
            final var xBounds = ranges[0].substring(2).split("\\.\\.");
            final var yBounds = ranges[1].substring(2).split("\\.\\.");
            final var zBounds = ranges[2].substring(2).split("\\.\\.");
            final var lowerX = Integer.parseInt(xBounds[0]);
            final var upperX = Integer.parseInt(xBounds[1]);
            final var lowerY = Integer.parseInt(yBounds[0]);
            final var upperY = Integer.parseInt(yBounds[1]);
            final var lowerZ = Integer.parseInt(zBounds[0]);
            final var upperZ = Integer.parseInt(zBounds[1]);
            if (on) {
                grid.switchRegionOn(lowerX, upperX, lowerY, upperY, lowerZ, upperZ);
            } else {
                grid.switchRegionOff(lowerX, upperX, lowerY, upperY, lowerZ, upperZ);

            }
        });
        assertThat(grid.size()).isEqualTo(590784L);
    }

    @Test
    void switchFilePart1() {
        final var grid = new ReactorGrid();
        readFile().limit(20).forEach(line -> {
            final var on = line.startsWith("on");
            final var commands = line.substring(line.indexOf(' ') + 1);
            final var ranges = commands.split(",");
            final var xBounds = ranges[0].substring(2).split("\\.\\.");
            final var yBounds = ranges[1].substring(2).split("\\.\\.");
            final var zBounds = ranges[2].substring(2).split("\\.\\.");
            final var lowerX = Integer.parseInt(xBounds[0]);
            final var upperX = Integer.parseInt(xBounds[1]);
            final var lowerY = Integer.parseInt(yBounds[0]);
            final var upperY = Integer.parseInt(yBounds[1]);
            final var lowerZ = Integer.parseInt(zBounds[0]);
            final var upperZ = Integer.parseInt(zBounds[1]);
            if (on) {
                grid.switchRegionOn(lowerX, upperX, lowerY, upperY, lowerZ, upperZ);
            } else {
                grid.switchRegionOff(lowerX, upperX, lowerY, upperY, lowerZ, upperZ);

            }
        });
        assertThat(grid.size()).isEqualTo(615869L);
    }

    @Test
    void switchFilePart2() {
        final var regions = new ArrayList<Region>();
        readFile().forEach(line -> {
            final var on = line.startsWith("on");
            final var commands = line.substring(line.indexOf(' ') + 1);
            final var ranges = commands.split(",");
            final var xBounds = ranges[0].substring(2).split("\\.\\.");
            final var yBounds = ranges[1].substring(2).split("\\.\\.");
            final var zBounds = ranges[2].substring(2).split("\\.\\.");
            final var lowerX = Integer.parseInt(xBounds[0]);
            final var upperX = Integer.parseInt(xBounds[1]);
            final var lowerY = Integer.parseInt(yBounds[0]);
            final var upperY = Integer.parseInt(yBounds[1]);
            final var lowerZ = Integer.parseInt(zBounds[0]);
            final var upperZ = Integer.parseInt(zBounds[1]);
            final var region = new Region(lowerX, upperX, lowerY, upperY, lowerZ, upperZ);
            final var doublesRemoved = new ArrayList<>(regions.stream().flatMap(r -> r.minusRegion(region)).toList());
            if (on) {
                doublesRemoved.add(region);
            } // no need for further removal
            regions.clear();
            regions.addAll(doublesRemoved);
        });
        final var litCubes = regions.stream().mapToLong(Region::numberOfCubes).sum();
        assertThat(litCubes).isEqualTo(1323862415207825L);
    }

    @Test
    void testExampleUsingRegions() {
        final var regions = new ArrayList<Region>();
        SAMPLE_INSTRUCTIONS.lines().limit(20).forEach(line -> {
            final var on = line.startsWith("on");
            final var commands = line.substring(line.indexOf(' ') + 1);
            final var ranges = commands.split(",");
            final var xBounds = ranges[0].substring(2).split("\\.\\.");
            final var yBounds = ranges[1].substring(2).split("\\.\\.");
            final var zBounds = ranges[2].substring(2).split("\\.\\.");
            final var lowerX = Integer.parseInt(xBounds[0]);
            final var upperX = Integer.parseInt(xBounds[1]);
            final var lowerY = Integer.parseInt(yBounds[0]);
            final var upperY = Integer.parseInt(yBounds[1]);
            final var lowerZ = Integer.parseInt(zBounds[0]);
            final var upperZ = Integer.parseInt(zBounds[1]);
            final var region = new Region(lowerX, upperX, lowerY, upperY, lowerZ, upperZ);
            final var doublesRemoved = new ArrayList<>(regions.stream().flatMap(r -> r.minusRegion(region)).toList());
            if (on) {
                doublesRemoved.add(region);
            } // no need for further removal
            regions.clear();
            regions.addAll(doublesRemoved);
        });
        final var litCubes = regions.stream().mapToLong(Region::numberOfCubes).sum();
        assertThat(litCubes).isEqualTo(590784L);
    }

    @Test
    void regionMinus_rightOf() {
        final var startRegion = new Region(0, 2, 0, 2, 0, 2);
        assertThat(startRegion.minusRegion(new Region(3, 5, 0, 2, 0, 2)).map(Region::bounds))
                .containsExactly(startRegion.bounds());
    }

    @Test
    void regionCubeCount() {
        assertThat(new Region(0,2,0,2,0,2).numberOfCubes()).isEqualTo(27);
    }

    @Test
    void testSimpleAddAndRemove() {
        final var initialThreeByThree = new Region(0, 2, 0, 2, 0, 2);
        final var overlappingCube = new Region(2,4,2,4,2,4);
        final var cubeToRemove = new Region(1,1,1,1,1,1);
        final var regions = new ArrayList<>(initialThreeByThree.minusRegion(overlappingCube).toList());
        regions.add(overlappingCube);
        final var finalRegions = regions.stream().flatMap(reg -> reg.minusRegion(cubeToRemove)).toList();
        assertThat(finalRegions.stream().mapToLong(Region::numberOfCubes).sum()).isEqualTo(52L);
    }

    @Test
    void regionMinus_behind() {
        final var startRegion = new Region(0, 2, 0, 2, 0, 2);
        assertThat(startRegion.minusRegion(new Region(0, 2, 0, 2, 3, 5)).map(Region::bounds))
                .containsExactly(startRegion.bounds());
    }

    @Test
    void regionMinus_onTop() {
        final var startRegion = new Region(0, 2, 0, 2, 0, 2);
        assertThat(startRegion.minusRegion(new Region(0, 2, 3, 5, 0, 2)).map(Region::bounds))
                .containsExactly(startRegion.bounds());
    }

    @Test
    void regionMinus_itself_isEmpty() {
        final var startRegion = new Region(0, 2, 0, 2, 0, 2);
        assertThat(startRegion.minusRegion(new Region(0, 2, 0, 2, 0, 2))).isEmpty();
    }

    @Test
    void regionMinus_leaveOneSlice_onX() {
        final var startRegion = new Region(0, 2, 0, 2, 0, 2);
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedDimenstions =
                expectedRegion(Tuple.of(0L, 0L, 0L), Tuple.of(0L, 2L, 2L));
        assertThat(startRegion.minusRegion(new Region(1, 2, 0, 2, 0, 2)).map(Region::bounds))
                .containsExactly(expectedDimenstions);
    }

    @Test
    void regionMinus_leaveOneSlice_onY() {
        final var startRegion = new Region(0, 2, 0, 2, 0, 2);
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedDimenstions =
                expectedRegion(Tuple.of(0L, 0L, 0L), Tuple.of(2L, 0L, 2L));
        assertThat(startRegion.minusRegion(new Region(0, 2, 1, 2, 0, 2)).map(Region::bounds))
                .containsExactly(expectedDimenstions);
    }

    @Test
    void regionMinus_takeOutMiddle() {
        final var startRegion = new Region(0, 2, 0, 2, 0, 2);
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedFrontSlice =
                expectedRegion(Tuple.of(0L, 0L, 0L), Tuple.of(2L, 2L, 0L));
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedBackslice =
                expectedRegion(Tuple.of(0L, 0L, 2L), Tuple.of(2L, 2L, 2L));
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedLeftColumn =
                expectedRegion(Tuple.of(0L, 0L, 1L), Tuple.of(0L, 2L, 1L));
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedRightColumn =
                expectedRegion(Tuple.of(2L, 0L, 1L), Tuple.of(2L, 2L, 1L));
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedBottomCube =
                expectedRegion(Tuple.of(1L, 0L, 1L), Tuple.of(1L, 0L, 1L));
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedTopCube =
                expectedRegion(Tuple.of(1L, 2L, 1L), Tuple.of(1L, 2L, 1L));
        assertThat(startRegion.minusRegion(new Region(1, 1, 1, 1, 1, 1)).map(Region::bounds))
                .containsExactlyInAnyOrder(expectedFrontSlice, expectedBackslice, expectedLeftColumn,
                        expectedRightColumn, expectedBottomCube, expectedTopCube);
    }

    @Test
    void regionMinus_takeOutMiddleColumnLargerThanOriginal() {
        final var startRegion = new Region(0, 2, 0, 2, 0, 2);
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedFrontSlice =
                expectedRegion(Tuple.of(0L, 0L, 0L), Tuple.of(2L, 2L, 0L));
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedBackslice =
                expectedRegion(Tuple.of(0L, 0L, 2L), Tuple.of(2L, 2L, 2L));
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedLeftColumn =
                expectedRegion(Tuple.of(0L, 0L, 1L), Tuple.of(0L, 2L, 1L));
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedRightColumn =
                expectedRegion(Tuple.of(2L, 0L, 1L), Tuple.of(2L, 2L, 1L));
        assertThat(startRegion.minusRegion(new Region(1, 1, -2, 4, 1, 1)).map(Region::bounds))
                .containsExactlyInAnyOrder(expectedFrontSlice, expectedBackslice, expectedLeftColumn,
                        expectedRightColumn);
    }

    @Test
    void regionMinus_cornerOverlap() {
        final var startRegion = new Region(0, 2, 0, 2, 0, 2);
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedFrontSlice =
                expectedRegion(Tuple.of(0L, 0L, 0L), Tuple.of(2L, 2L, 1L));
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedLeftColumn =
                expectedRegion(Tuple.of(0L, 0L, 2L), Tuple.of(1L, 2L, 2L));
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedBottomCube =
                expectedRegion(Tuple.of(2L, 0L, 2L), Tuple.of(2L, 1L, 2L));
        assertThat(startRegion.minusRegion(new Region(2, 4, 2, 4, 2, 4)).map(Region::bounds))
                .containsExactlyInAnyOrder(expectedFrontSlice, expectedLeftColumn, expectedBottomCube);
    }

    private Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedRegion(
            final Tuple3<Long, Long, Long> expectedLowerBound, final Tuple3<Long, Long, Long> expectedUpperBound) {
        return Tuple.of(expectedLowerBound, expectedUpperBound);
    }

    @Test
    void regionMinus_leaveOneSlice_onZ() {
        final var startRegion = new Region(0, 2, 0, 2, 0, 2);
        final Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> expectedDimenstions =
                expectedRegion(Tuple.of(0L, 0L, 0L), Tuple.of(2L, 2L, 0L));
        assertThat(startRegion.minusRegion(new Region(0, 2, 0, 2, 1, 2)).map(Region::bounds))
                .containsExactly(expectedDimenstions);
    }

    private Stream<String> readFile() {
        var inputStream = Day22.class.getResourceAsStream("/day22.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    class ReactorGrid {

        private final Set<Tuple3<Long, Long, Long>> cuboids = new HashSet<>();

        void switchOn(long x, long y, long z) {
            cuboids.add(new Tuple3<>(x, y, z));
        }

        void switchOff(long x, long y, long z) {
            cuboids.remove(new Tuple3<>(x, y, z));
        }

        public long size() {
            return cuboids.size();
        }

        private void switchRegionOn(final int lowerX, final int upperX, final int lowerY,
                final int upperY, final int lowerZ, final int upperZ) {
            for (int x = lowerX; x <= upperX; x++) {
                for (int y = lowerY; y <= upperY; y++) {
                    for (int z = lowerZ; z <= upperZ; z++) {
                        switchOn(x, y, z);
                    }
                }
            }
        }

        private void switchRegionOff(final int lowerX, final int upperX, final int lowerY,
                final int upperY, final int lowerZ, final int upperZ) {
            for (int x = lowerX; x <= upperX; x++) {
                for (int y = lowerY; y <= upperY; y++) {
                    for (int z = lowerZ; z <= upperZ; z++) {
                        switchOff(x, y, z);
                    }
                }
            }
        }
    }

    class Region {

        private final long lowerX;
        private final long upperX;
        private final long lowerY;
        private final long upperY;
        private final long lowerZ;
        private final long upperZ;

        Region(final long lowerX, final long upperX, final long lowerY, final long upperY, final long lowerZ,
                final long upperZ) {
            this.lowerX = lowerX;
            this.upperX = upperX;
            this.lowerY = lowerY;
            this.upperY = upperY;
            this.lowerZ = lowerZ;
            this.upperZ = upperZ;
        }

        Stream<Region> minusRegion(final Region other) {
            final var noXintersection = this.lowerX > other.upperX || this.upperX < other.lowerX;
            final var noYIntersection = this.lowerY > other.upperY || this.upperY < other.lowerY;
            final var noZIntersection = this.lowerZ > other.upperZ || this.upperZ < other.lowerZ;
            if (noXintersection || noYIntersection || noZIntersection) {
                return Stream.of(this);
            }
            final var keepLeftOfThis = other.lowerX;
            final var keepRightOfThis = other.upperX;
            final var keepBelowThis = other.lowerY;
            final var keepAboveThis = other.upperY;
            final var keepInFrontOfThis = other.lowerZ;
            final var keepBehindThis = other.upperZ;

            final var frontSlice = new Region(lowerX, upperX, lowerY, upperY, lowerZ, keepInFrontOfThis - 1);
            final var backSlice = new Region(lowerX, upperX, lowerY, upperY, keepBehindThis + 1, upperZ);
            final var leftSandwichedColumn =
                    new Region(lowerX, keepLeftOfThis - 1, lowerY, upperY, Math.max(keepInFrontOfThis, lowerZ), Math.min(keepBehindThis, upperZ));
            final var rightSandwichedColumn =
                    new Region(keepRightOfThis + 1, upperX, lowerY, upperY, Math.max(keepInFrontOfThis, lowerZ), Math.min(keepBehindThis, upperZ));
            final var bottomMiddleBlock =
                    new Region(Math.max(keepLeftOfThis, lowerX), Math.min(keepRightOfThis, upperX), lowerY, keepBelowThis - 1, Math.max(keepInFrontOfThis, lowerZ), Math.min(keepBehindThis, upperZ));
            final var topMiddleBlock =
                    new Region(Math.max(keepLeftOfThis, lowerX), Math.min(keepRightOfThis, upperX), keepAboveThis + 1, upperY, Math.max(keepInFrontOfThis, lowerZ), Math.min(keepBehindThis, upperZ));
            return Stream.of(frontSlice, backSlice, leftSandwichedColumn, rightSandwichedColumn, bottomMiddleBlock,
                    topMiddleBlock).filter(block -> !block.isEmpty());
        }

        Tuple2<Tuple3<Long, Long, Long>, Tuple3<Long, Long, Long>> bounds() {
            return Tuple.of(Tuple.of(lowerX, lowerY, lowerZ), Tuple.of(upperX, upperY, upperZ));
        }

        private boolean isEmpty() {
            return this.upperX - this.lowerX < 0 || this.upperY - lowerY < 0 || this.upperZ - lowerZ < 0;
        }

        @Override
        public String toString() {
            return "Region{" +
                    "lowerX=" + lowerX +
                    ", upperX=" + upperX +
                    ", lowerY=" + lowerY +
                    ", upperY=" + upperY +
                    ", lowerZ=" + lowerZ +
                    ", upperZ=" + upperZ +
                    '}';
        }

        public long numberOfCubes() {
            final long xAmount = upperX - lowerX + 1;
            final long yAmount = upperY - lowerY + 1;
            final long zAmount = upperZ - lowerZ + 1;
            return xAmount * yAmount * zAmount;
        }
    }

}
