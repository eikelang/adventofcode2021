package day19;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.vavr.Tuple;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

class Day19 {

    private static final String TOKENIZATION_INPUT = """
            --- scanner 0 ---
            653,279,-501
            679,389,-558
                        
            --- scanner 1 ---
            -425,386,591
            -449,587,580
                        
            --- scanner 2 ---
            -433,-534,544
            -435,-569,466""";

    private static final String MINI_EXAMPLE = """
            --- scanner 0 ---
            0,2,0
            4,1,0
            3,3,0
                        
            --- scanner 1 ---
            -1,-1,0
            -5,0,0
            -2,1,0""";

    private static final String EXAMPLE = """
            --- scanner 0 ---
            404,-588,-901
            528,-643,409
            -838,591,734
            390,-675,-793
            -537,-823,-458
            -485,-357,347
            -345,-311,381
            -661,-816,-575
            -876,649,763
            -618,-824,-621
            553,345,-567
            474,580,667
            -447,-329,318
            -584,868,-557
            544,-627,-890
            564,392,-477
            455,729,728
            -892,524,684
            -689,845,-530
            423,-701,434
            7,-33,-71
            630,319,-379
            443,580,662
            -789,900,-551
            459,-707,401
                        
            --- scanner 1 ---
            686,422,578
            605,423,415
            515,917,-361
            -336,658,858
            95,138,22
            -476,619,847
            -340,-569,-846
            567,-361,727
            -460,603,-452
            669,-402,600
            729,430,532
            -500,-761,534
            -322,571,750
            -466,-666,-811
            -429,-592,574
            -355,545,-477
            703,-491,-529
            -328,-685,520
            413,935,-424
            -391,539,-444
            586,-435,557
            -364,-763,-893
            807,-499,-711
            755,-354,-619
            553,889,-390
                        
            --- scanner 2 ---
            649,640,665
            682,-795,504
            -784,533,-524
            -644,584,-595
            -588,-843,648
            -30,6,44
            -674,560,763
            500,723,-460
            609,671,-379
            -555,-800,653
            -675,-892,-343
            697,-426,-610
            578,704,681
            493,664,-388
            -671,-858,530
            -667,343,800
            571,-461,-707
            -138,-166,112
            -889,563,-600
            646,-828,498
            640,759,510
            -630,509,768
            -681,-892,-333
            673,-379,-804
            -742,-814,-386
            577,-820,562
                        
            --- scanner 3 ---
            -589,542,597
            605,-692,669
            -500,565,-823
            -660,373,557
            -458,-679,-417
            -488,449,543
            -626,468,-788
            338,-750,-386
            528,-832,-391
            562,-778,733
            -938,-730,414
            543,643,-506
            -524,371,-870
            407,773,750
            -104,29,83
            378,-903,-323
            -778,-728,485
            426,699,580
            -438,-605,-362
            -469,-447,-387
            509,732,623
            647,635,-688
            -868,-804,481
            614,-800,639
            595,780,-596
                        
            --- scanner 4 ---
            727,592,562
            -293,-554,779
            441,611,-461
            -714,465,-776
            -743,427,-804
            -660,-479,-426
            832,-632,460
            927,-485,-438
            408,393,-506
            466,436,-512
            110,16,151
            -258,-428,682
            -393,719,612
            -211,-452,876
            808,-476,-593
            -575,615,604
            -485,667,467
            -680,325,-822
            -627,-443,-432
            872,-547,-609
            833,512,582
            807,604,487
            839,-516,451
            891,-625,532
            -652,-548,-490
            30,-46,-14""";

    private Stream<String> readFile() {
        var inputStream = Day19.class.getResourceAsStream("/day19.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        Scanner scannerScanner = new Scanner(buf);
        scannerScanner.useDelimiter("--- scanner \\d+ ---");
        return scannerScanner.tokens();
    }

    private Stream<String> readString(final String input) {
        var fis = new StringReader(input);
        var buf = new BufferedReader(fis);
        Scanner scannerScanner = new Scanner(buf);
        scannerScanner.useDelimiter("--- scanner \\d+ ---");
        return scannerScanner.tokens();
    }

    @Test
    void testScannerTokenization() {
        final var inStream = new StringReader(TOKENIZATION_INPUT);
        final List<String> result = tokenizeInput(inStream);
        assertThat(result.get(0).lines().filter(s -> !s.isEmpty()).toList()).containsExactly(
                "653,279,-501", "679,389,-558");
    }

    private List<String> tokenizeInput(final StringReader inStream) {
        var buf = new BufferedReader(inStream);
        Scanner scannerScanner = new Scanner(buf);
        scannerScanner.useDelimiter("--- scanner \\d+ ---");
        return scannerScanner.tokens().toList();
    }

    @Test
    void testScannerTokenizationFromFile() {
        final var result = readFile().toList();
        assertThat(result.get(0).lines().filter(s -> !s.isEmpty()).toList()).contains(
                "653,279,-501", "679,389,-558");
    }

    @Test
    void testRotationIdentity() {
        final var initialRepresentation = new RelativePosition(5, 10, 13);
        final var notReallyRotated = initialRepresentation.rotateBy(0, 0, 0);
        assertThat(notReallyRotated).isEqualTo(initialRepresentation);
    }

    @Test
    void testZRotation90degrees() {
        final var initialRepresentation = new RelativePosition(5, 10, 13);
        final var notReallyRotated = initialRepresentation.rotateBy(0, 0, 1);
        assertThat(notReallyRotated).isEqualTo(new RelativePosition(10, -5, 13));
    }

    @Test
    void testZRotation180degrees() {
        final var initialRepresentation = new RelativePosition(5, 10, 13);
        final var notReallyRotated = initialRepresentation.rotateBy(0, 0, 2);
        assertThat(notReallyRotated).isEqualTo(new RelativePosition(-5, -10, 13));
    }

    @Test
    void testZRotation270degrees() {
        final var initialRepresentation = new RelativePosition(5, 10, 13);
        final var notReallyRotated = initialRepresentation.rotateBy(0, 0, 3);
        assertThat(notReallyRotated).isEqualTo(new RelativePosition(-10, 5, 13));
    }

    @Test
    void testYRotation90degrees() {
        final var initialRepresentation = new RelativePosition(5, 10, 13);
        final var notReallyRotated = initialRepresentation.rotateBy(0, 1, 0);
        assertThat(notReallyRotated).isEqualTo(new RelativePosition(13, 10, -5));
    }

    @Test
    void testYRotation180degrees() {
        final var initialRepresentation = new RelativePosition(5, 10, 13);
        final var notReallyRotated = initialRepresentation.rotateBy(0, 2, 0);
        assertThat(notReallyRotated).isEqualTo(new RelativePosition(-5, 10, -13));
    }

    @Test
    void testYRotation270degrees() {
        final var initialRepresentation = new RelativePosition(5, 10, 13);
        final var notReallyRotated = initialRepresentation.rotateBy(0, 3, 0);
        assertThat(notReallyRotated).isEqualTo(new RelativePosition(-13, 10, 5));
    }

    @Test
    void testXRotation90degrees() {
        final var initialRepresentation = new RelativePosition(5, 10, 13);
        final var notReallyRotated = initialRepresentation.rotateBy(1, 0, 0);
        assertThat(notReallyRotated).isEqualTo(new RelativePosition(5, -13, 10));
    }

    @Test
    void relativeDistanceIsZeroForSelf() {
        final var position = new RelativePosition(3, 7, -23);
        assertThat(position.relativeTo(position)).isEqualTo(new RelativePosition(0, 0, 0));
    }

    @Test
    void relativeDistanceToZeroIsSelf() {
        final var position = new RelativePosition(3, 7, -23);
        assertThat(position.relativeTo(new RelativePosition(0, 0, 0))).isEqualTo(position);
    }

    @Test
    void relativeDistanceToPointCloserToOrigin() {
        final var position = new RelativePosition(2, 7, 5);
        final var newOrigin = new RelativePosition(1, 6, 3);
        assertThat(position.relativeTo(newOrigin)).isEqualTo(new RelativePosition(1, 1, 2));
    }

    @Test
    void parseScanners() {
        final List<ProbeScanner> scanners = readScanners(readFile());
        assertThat(scanners).hasSize(38);
        assertThat(scanners.get(0).getDetectedProbes()).contains(new RelativePosition(-519, -440, -646));
    }

    @Test
    void testSignatures() {
        final List<ProbeScanner> scanners = readScanners(readString(EXAMPLE));
        final var scanner0 = scanners.get(0);
        final var scanner1 = scanners.get(1);

        final var sigs0 = scanner0.signatures();
        final var sigs1 = scanner1.signatures();

        final var keyset0 = new HashSet<>(sigs0.keySet());
        keyset0.retainAll(sigs1.keySet());
        assertThat(keyset0).hasSize(66);
    }

    @Test
    void fullExampleUsingSignatures() {
        final List<ProbeScanner> scanners = readScanners(readString(EXAMPLE));

        var totalCoordinates = scanners.stream().mapToInt(scanner -> scanner.getDetectedProbes().size()).sum();

        var overlapMapping = new HashMap<Integer, Set<Integer>>();
        var totalSum = totalCoordinates;
        for (int i = 0; i < scanners.size(); i++) {
            for (int j = i; j < scanners.size(); j++) {
                if (overlaps(scanners.get(i), scanners.get(j))) {
                    overlapMapping.merge(i, singleton(j), (s1, s2) -> {
                        final var newValue = new HashSet<>(s1);
                        newValue.addAll(s2);
                        return newValue;
                    });
                    System.out.println("Scanner " + i + " overlaps scanner " + j);
                    totalSum -= 12;
                }
            }
        }
        final int alternativeSum = overlapMapping.entrySet().stream().mapToInt(e -> {
            final var totalPoints = e.getValue().stream().mapToInt(
                    i -> scanners.get(i).getDetectedProbes().size() + scanners.get(e.getKey()).getDetectedProbes()
                            .size()).sum();
            return totalPoints - e.getValue().size() * 12;
        }).sum();
        assertThat(totalSum).isEqualTo(79);
        assertThat(alternativeSum).isEqualTo(79);
    }

    @Test
    void tryAll() {
        final List<ProbeScanner> scanners = readScanners(readFile());

        final var reduced = scanners.stream().map(ProbeScanner::signatures)
                .reduce((m1, m2) -> {
                    final var newMap = new HashMap<>(m1);
                    m2.forEach((key, value) -> newMap.merge(key, value, (sets, sets2) -> {
                        final var newSet = new HashSet<>(sets);
                        newSet.addAll(sets2);
                        return newSet;
                    }));
                    return newMap;
                });
        var totalCoordinates = scanners.stream().mapToInt(scanner -> scanner.getDetectedProbes().size()).sum();

        var totalSum = totalCoordinates;
        for (int i = 0; i < scanners.size(); i++) {
            for (int j = i; j < scanners.size(); j++) {
                if (overlaps(scanners.get(i), scanners.get(j))) {
                    System.out.println("Scanner " + i + " overlaps scanner " + j);
                    totalSum -= 12;
                }

            }
        }
        assertThat(totalSum).isEqualTo(79);
    }

    private boolean overlaps(final ProbeScanner scanner1, final ProbeScanner scanner2) {
        final var sigs1 = scanner1.signatures();
        final var sigs2 = scanner2.signatures();

        final var keyset0 = new HashSet<>(sigs1.keySet());
        keyset0.retainAll(sigs2.keySet());
        return keyset0.size() >= 66;
    }

    private Scanner relativeTo(final ProbeScanner scanner1, final ProbeScanner scanner2) {
        final var sigs1 = scanner1.signatures();
        final var sigs2 = scanner2.signatures();

        final var sharedDistances = new HashSet<>(sigs1.keySet());
        sharedDistances.retainAll(sigs2.keySet());
        return null;

    }

    @Test
    void testIntersectionOnXAxisMini() {
        final List<ProbeScanner> scanners = readScanners(readString(MINI_EXAMPLE));
        final var scanner0 = scanners.get(0);
        final var scanner1 = scanners.get(1);
        assertThat(scanner0.intersectsOnX(scanner1, 3)).isTrue();
    }

    @Test
    void filterDoubleRotations() {
        final RelativePosition pos = new RelativePosition(1, 2, 3);
        final var allPositions = IntStream.rangeClosed(0, 3).boxed()
                .flatMap(x -> IntStream.rangeClosed(0, 3).boxed()
                        .flatMap(y -> IntStream.rangeClosed(0, 3)
                                .mapToObj(z -> Tuple.of(
                                        String.join(",", Integer.toString(x), Integer.toString(y), Integer.toString(z)),
                                        pos.rotateBy(x, y, z))))).collect(Collectors.toMap(t -> t._1, t -> t._2));

        final var resultsByTransform = allPositions.entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(
                        Map.Entry::getKey, Collectors.toSet())));
        System.out.println("End");
    }

    private List<ProbeScanner> readScanners(final Stream<String> inputStream) {
        final var input = inputStream.toList();
        final var scanners = input.stream().map(
                        s -> {
                            final var scanner = new ProbeScanner();
                            s.lines().filter(line -> !line.isEmpty())
                                    .map(line -> line.split(","))
                                    .map(a -> new RelativePosition(Integer.parseInt(a[0]),
                                            Integer.parseInt(a[1]),
                                            Integer.parseInt(a[2])))
                                    .forEach(scanner::addBeaconPosition);
                            return scanner;
                        })
                .toList();
        return scanners;
    }

    @Test
    @Disabled
    void exploreRelativeStuff() {
        final var probeScanners = readScanners(readFile());
        for (final var scanner : probeScanners) {
            for (final var innerScanner : probeScanners) {
                if (scanner == innerScanner) {
                    continue;
                }
                scanner.intersectsWith(innerScanner);
            }
        }
    }

    @Test
    @Disabled
    void exploreRelativeStuffDistanceBased() {
        final var probeScanners = readScanners(readFile());
        for (final var scanner : probeScanners) {
            for (final var innerScanner : probeScanners) {
                if (scanner == innerScanner) {
                    continue;
                }
                final var scannersMatch = innerScanner.allPermutations().stream()
                        .anyMatch(scanner::intersectsWithBasedOnDistances);
            }
        }
    }

    @Test
    void aScannerIntersectsWithItself() {
        final var scanner = readScanners(readFile()).get(0);
        assertThat(scanner.intersectsWith(scanner)).isTrue();
    }

}
