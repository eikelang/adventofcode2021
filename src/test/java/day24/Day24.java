package day24;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class Day24 {

    private static final String MULTIPLES_PROGRAM = """
            inp z
            inp x
            mul z 3
            eql z x""";

    private static final String BINARY_DIGITS_PROGRAM = """
            inp w
            add z w
            mod z 2
            div w 2
            add y w
            mod y 2
            div w 2
            add x w
            mod x 2
            div w 2
            mod w 2""";

    private static final String THIRD_BEFORE_LAST = """
            inp w
            mul x 0
            add x z
            mod x 26
            div z 26
            add x -14
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 6
            mul y x
            add z y""";

    private static final String SECOND_BEFORE_LAST = """
            inp w
            mul x 0
            add x z
            mod x 26
            div z 26
            add x -3
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 7
            mul y x
            add z y""";

    private static final String BLOCK_BEFORE_LAST = """
            inp w
            mul x 0
            add x z
            mod x 26
            div z 26
            add x -2
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 13
            mul y x
            add z y""";

    private static final String LAST_BLOCK = """
            inp w
            mul x 0
            add x z
            mod x 26
            div z 26
            add x -14
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 3
            mul y x
            add z y""";

    private static final String FIRST_TWO_BLOCKS = """
            inp w
            mul x 0
            add x z
            mod x 26
            div z 1
            add x 13
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 14
            mul y x
            add z y
            inp w
            mul x 0
            add x z
            mod x 26
            div z 1
            add x 12
            eql x w
            eql x 0
            mul y 0
            add y 25
            mul y x
            add y 1
            mul z y
            mul y 0
            add y w
            add y 8
            mul y x
            add z y""";

    private static final String FIRST_FOUR_BLOCKS =
            FIRST_TWO_BLOCKS + "\n" +
                    """
                            inp w
                            mul x 0
                            add x z
                            mod x 26
                            div z 1
                            add x 11
                            eql x w
                            eql x 0
                            mul y 0
                            add y 25
                            mul y x
                            add y 1
                            mul z y
                            mul y 0
                            add y w
                            add y 5
                            mul y x
                            add z y
                            inp w
                            mul x 0
                            add x z
                            mod x 26
                            div z 26
                            add x 0
                            eql x w
                            eql x 0
                            mul y 0
                            add y 25
                            mul y x
                            add y 1
                            mul z y
                            mul y 0
                            add y w
                            add y 4
                            mul y x
                            add z y""";

    private static final String FIRST_SEVEN_BLOCKS = FIRST_FOUR_BLOCKS + "\n" +
            """
                    inp w
                    mul x 0
                    add x z
                    mod x 26
                    div z 1
                    add x 15
                    eql x w
                    eql x 0
                    mul y 0
                    add y 25
                    mul y x
                    add y 1
                    mul z y
                    mul y 0
                    add y w
                    add y 10
                    mul y x
                    add z y
                    inp w
                    mul x 0
                    add x z
                    mod x 26
                    div z 26
                    add x -13
                    eql x w
                    eql x 0
                    mul y 0
                    add y 25
                    mul y x
                    add y 1
                    mul z y
                    mul y 0
                    add y w
                    add y 13
                    mul y x
                    add z y
                    inp w
                    mul x 0
                    add x z
                    mod x 26
                    div z 1
                    add x 10
                    eql x w
                    eql x 0
                    mul y 0
                    add y 25
                    mul y x
                    add y 1
                    mul z y
                    mul y 0
                    add y w
                    add y 16
                    mul y x
                    add z y""";

    private Stream<String> readFile() {
        var inputStream = Day24.class.getResourceAsStream("/day24.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    @Test
    void testMultiple() {
        final var alu = new Alu(asList(3, 9));
        MULTIPLES_PROGRAM.lines().forEach(alu::executeInstruction);
        assertThat(alu.zRegister()).isEqualTo(1);
    }

    @Test
    void computeBinaryDigits() {
        final var alu = new Alu(singletonList(4));
        BINARY_DIGITS_PROGRAM.lines().forEach(alu::executeInstruction);
        assertThat(alu.wRegister()).isEqualTo(0);
        assertThat(alu.xRegister()).isEqualTo(1);
        assertThat(alu.yRegister()).isEqualTo(0);
        assertThat(alu.zRegister()).isEqualTo(0);
    }

    @Test
    void testAddingNegativeDigits() {
        final var alu = new Alu(singletonList(4));
        alu.executeInstruction("add x -1");
        assertThat(alu.xRegister()).isEqualTo(-1);
    }

    @Test
    void testMultiplyingNegativeDigits() {
        final var alu = new Alu(singletonList(4));
        alu.executeInstruction("add x 1");
        alu.executeInstruction("mul x -1");
        assertThat(alu.xRegister()).isEqualTo(-1);
    }

    @Test
    void initialTwoSteps() {
        for (int i = 0; i < 100; i++) {
            final var stringInput = String.format("%02d", i);
            if (stringInput.contains("0")) {
                continue;
            }
            final var parts = stringInput.split("");
            final var input = Arrays.stream(parts).map(Integer::parseInt).toList();
            final var alu = new Alu(input);
            FIRST_TWO_BLOCKS.lines().forEach(alu::executeInstruction);
            System.out.println(stringInput + ": " + alu.zRegister());
        }
    }

    @Test
    void initialFourSteps() {
        final var resultSet = new HashSet<Integer>();
        final var resultsGroupedByOutput = new HashMap<Integer, Set<String>>();
        for (int i = 0; i < 10000; i++) {
            final var stringInput = String.format("%04d", i);
            if (stringInput.contains("0")) {
                continue;
            }
            final var parts = stringInput.split("");
            final var input = Arrays.stream(parts).map(Integer::parseInt).toList();
            final var alu = new Alu(input);
            FIRST_FOUR_BLOCKS.lines().forEach(alu::executeInstruction);
            resultsGroupedByOutput.merge(alu.zRegister(), singleton(stringInput),
                    (k, v) -> Stream.concat(v.stream(), Stream.of(stringInput))
                            .collect(Collectors.toSet()));
            resultSet.add(alu.zRegister());

        }
        System.out.println(resultSet);
    }

    @Test
    void firstSevenSteps() {
        final var resultSet = new HashSet<Integer>();
        final var resultsGroupedByOutput = new HashMap<Integer, Set<String>>();
        for (int i = 0; i < 10000000; i++) {
            final var stringInput = String.format("%07d", i);
            if (stringInput.contains("0")) {
                continue;
            }
            final var parts = stringInput.split("");
            final var input = Arrays.stream(parts).map(Integer::parseInt).toList();
            final var alu = new Alu(input);
            FIRST_SEVEN_BLOCKS.lines().forEach(alu::executeInstruction);
            resultsGroupedByOutput.merge(alu.zRegister(), singleton(stringInput),
                    (k, v) -> Stream.concat(v.stream(), Stream.of(stringInput))
                            .collect(Collectors.toSet()));
            resultSet.add(alu.zRegister());

        }
        System.out.println(
                resultSet.stream().filter(i -> i < 99999).sorted().map(i -> i + ": " + resultsGroupedByOutput.get(i))
                        .toList());
    }

    @Test
    void setThirdBeforeLast() {
        final var desiredZValues =
                Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
                        30, 31, 32, 33, 34, 35, 36, 37, 38,
                        56, 57, 58, 59, 60, 61, 62, 63, 64,
                        82, 83, 84, 85, 86, 87, 88, 89, 90,
                        108, 109, 110, 111, 112, 113, 114, 115, 116,
                        134, 135, 136, 137, 138, 139, 140, 141, 142,
                        160, 161, 162, 163, 164, 165, 166, 167, 168,
                        186, 187, 188, 189, 190, 191, 192, 193, 194,
                        212, 213, 214, 215, 216, 217, 218, 219, 220,
                        238, 239, 240, 241, 242, 243, 244, 245, 246,
                        264, 265, 266, 267, 268, 269, 270, 271, 272,
                        290, 291, 292, 293, 294, 295, 296, 297, 298,
                        316, 317, 318, 319, 320, 321, 322, 323, 324,
                        342, 343, 344, 345, 346, 347, 348, 349, 350,
                        368, 369, 370, 371, 372, 373, 374, 375, 376,
                        394, 395, 396, 397, 398, 399, 400, 401, 402,
                        420, 421, 422, 423, 424, 425, 426, 427, 428,
                        446, 447, 448, 449, 450, 451, 452, 453, 454,
                        472, 473, 474, 475, 476, 477, 478, 479, 480,
                        498, 499, 500, 501, 502, 503, 504, 505, 506,
                        524, 525, 526, 527, 528, 529, 530, 531, 532,
                        550, 551, 552, 553, 554, 555, 556, 557, 558,
                        576, 577, 578, 579, 580, 581, 582, 583, 584,
                        602, 603, 604, 605, 606, 607, 608, 609, 610,
                        628, 629, 630, 631, 632, 633, 634, 635, 636,
                        654, 655, 656, 657, 658, 659, 660, 661, 662,
                        10222, 10223, 10224, 10225, 10226, 10227, 10228, 10229, 10230,
                        10248, 10249, 10250, 10251, 10252, 10253, 10254, 10255, 10256,
                        10274, 10275, 10276, 10277, 10278, 10279, 10280, 10281, 10282,
                        10300, 10301, 10302, 10303, 10304, 10305, 10306, 10307, 10308,
                        10326, 10327, 10328, 10329, 10330, 10331, 10332, 10333, 10334,
                        10352, 10353, 10354, 10355, 10356, 10357, 10358, 10359, 10360,
                        10378, 10379, 10380, 10381, 10382, 10383, 10384, 10385, 10386,
                        10404, 10405, 10406, 10407, 10408, 10409, 10410, 10411, 10412,
                        10430, 10431, 10432, 10433, 10434, 10435, 10436, 10437, 10438,
                        10898, 10899, 10900, 10901, 10902, 10903, 10904, 10905, 10906,
                        10924, 10925, 10926, 10927, 10928, 10929, 10930, 10931, 10932,
                        10950, 10951, 10952, 10953, 10954, 10955, 10956, 10957, 10958,
                        10976, 10977, 10978, 10979, 10980, 10981, 10982, 10983, 10984,
                        11002, 11003, 11004, 11005, 11006, 11007, 11008, 11009, 11010,
                        11028, 11029, 11030, 11031, 11032, 11033, 11034, 11035, 11036,
                        11054, 11055, 11056, 11057, 11058, 11059, 11060, 11061, 11062,
                        11080, 11081, 11082, 11083, 11084, 11085, 11086, 11087, 11088,
                        11106, 11107, 11108, 11109, 11110, 11111, 11112, 11113, 11114,
                        11574, 11575, 11576, 11577, 11578, 11579, 11580, 11581, 11582,
                        11600, 11601, 11602, 11603, 11604, 11605, 11606, 11607, 11608,
                        11626, 11627, 11628, 11629, 11630, 11631, 11632, 11633, 11634,
                        11652, 11653, 11654, 11655, 11656, 11657, 11658, 11659, 11660,
                        11678, 11679, 11680, 11681, 11682, 11683, 11684, 11685, 11686,
                        11704, 11705, 11706, 11707, 11708, 11709, 11710, 11711, 11712,
                        11730, 11731, 11732, 11733, 11734, 11735, 11736, 11737, 11738,
                        11756, 11757, 11758, 11759, 11760, 11761, 11762, 11763, 11764,
                        11782, 11783, 11784, 11785, 11786, 11787, 11788, 11789, 11790,
                        12250, 12251, 12252, 12253, 12254, 12255, 12256, 12257, 12258,
                        12276, 12277, 12278, 12279, 12280, 12281, 12282, 12283, 12284,
                        12302, 12303, 12304, 12305, 12306, 12307, 12308, 12309, 12310,
                        12328, 12329, 12330, 12331, 12332, 12333, 12334, 12335, 12336,
                        12354, 12355, 12356, 12357, 12358, 12359, 12360, 12361, 12362,
                        12380, 12381, 12382, 12383, 12384, 12385, 12386, 12387, 12388,
                        12406, 12407, 12408, 12409, 12410, 12411, 12412, 12413, 12414,
                        12432, 12433, 12434, 12435, 12436, 12437, 12438, 12439, 12440,
                        12458, 12459, 12460, 12461, 12462, 12463, 12464, 12465, 12466,
                        12926, 12927, 12928, 12929, 12930, 12931, 12932, 12933, 12934,
                        12952, 12953, 12954, 12955, 12956, 12957, 12958, 12959, 12960,
                        12978, 12979, 12980, 12981, 12982, 12983, 12984, 12985, 12986,
                        13004, 13005, 13006, 13007, 13008, 13009, 13010, 13011, 13012,
                        13030, 13031, 13032, 13033, 13034, 13035, 13036, 13037, 13038,
                        13056, 13057, 13058, 13059, 13060, 13061, 13062, 13063, 13065,
                        13082, 13083, 13084, 13085, 13086, 13087, 13088, 13089, 13090,
                        13108, 13109, 13110, 13111, 13112, 13113, 13114, 13115, 13116,
                        13134, 13135, 13136, 13137, 13138, 13139, 13140, 13141, 13142,
                        13602, 13603, 13604, 13605, 13606, 13607, 13608, 13609, 13610,
                        13628, 13629, 13630, 13631, 13632, 13633, 13634, 13635, 13636,
                        13654, 13655, 13656, 13657, 13658, 13659, 13660, 13661, 13662,
                        13680, 13681, 13682, 13683, 13684, 13685, 13686, 13687, 13688,
                        13706, 13707, 13708, 13709, 13710, 13711, 13712, 13713, 13714,
                        13732, 13733, 13734, 13735, 13736, 13737, 13738, 13739, 13740,
                        13758, 13759, 13760, 13761, 13762, 13763, 13764, 13765, 13766,
                        13784, 13785, 13786, 13787, 13788, 13789, 13790, 13791, 13792,
                        13810, 13811, 13812, 13813, 13814, 13815, 13816, 13817, 13818,
                        14278, 14279, 14280, 14281, 14282, 14283, 14284, 14285, 14286,
                        14304, 14305, 14306, 14307, 14308, 14309, 14310, 14311, 14312,
                        14330, 14331, 14332, 14333, 14334, 14335, 14336, 14337, 14338,
                        14356, 14357, 14358, 14359, 14360, 14361, 14362, 14363, 14364,
                        14382, 14383, 14384, 14385, 14386, 14387, 14388, 14389, 14390,
                        14408, 14409, 14410, 14411, 14412, 14413, 14414, 14415, 14416,
                        14434, 14435, 14436, 14437, 14438, 14439, 14440, 14441, 14442,
                        14460, 14461, 14462, 14463, 14464, 14465, 14466, 14467, 14468,
                        14486, 14487, 14488, 14489, 14490, 14491, 14492, 14493, 14494,
                        14954, 14955, 14956, 14957, 14958, 14959, 14960, 14961, 14962,
                        14980, 14981, 14982, 14983, 14984, 14985, 14986, 14987, 14988,
                        15006, 15007, 15008, 15009, 15010, 15011, 15012, 15013, 15014,
                        15032, 15033, 15034, 15035, 15036, 15037, 15038, 15039, 15040,
                        15058, 15059, 15060, 15061, 15062, 15063, 15064, 15065, 15066,
                        15084, 15085, 15086, 15087, 15088, 15089, 15090, 15091, 15092,
                        15110, 15111, 15112, 15113, 15114, 15115, 15116, 15117, 15118,
                        15136, 15137, 15138, 15139, 15140, 15141, 15142, 15143, 14144,
                        15162, 15163, 15164, 15165, 15166, 15167, 15168, 15169, 15170,
                        15630, 15631, 15632, 15633, 15634, 15635, 15636, 15637, 15638,
                        15656, 15657, 15658, 15659, 15660, 15661, 15662, 15663, 15664,
                        15682, 15683, 15684, 15685, 15686, 15687, 15688, 15689, 15690,
                        15708, 15709, 15710, 15711, 15712, 15713, 15714, 15715, 15716,
                        15734, 15735, 15736, 15737, 15738, 15739, 15740, 15741, 15742,
                        15760, 15761, 15762, 15763, 15764, 15765, 15766, 15767, 15768,
                        15786, 15787, 15788, 15789, 15790, 15791, 15792, 15793, 15794,
                        15812, 15813, 15814, 15815, 15816, 15817, 15818, 15819, 15820,
                        15838, 15839, 15840, 15841, 15842, 15843, 15844, 15845, 15846);
        for (int i = 0; i < 1000000; i++) {
            for (int w = 1; w < 10; w++) {
                final var alu = new Alu(singletonList(w));
                alu.setZRegister(i);
                THIRD_BEFORE_LAST.lines().forEach(alu::executeInstruction);
                if (desiredZValues.contains(alu.zRegister())) {
                    System.out.println("z: " + i + ", w: " + w);
                }
            }
        }
    }

    @Test
    void secondBeforeLast() {
        final var desiredZValues =
                Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
                        393, 394, 395, 396, 397, 398, 399, 400, 401,
                        419, 420, 421, 422, 423, 424, 425, 426, 427,
                        445, 446, 447, 448, 449, 450, 451, 452, 453,
                        471, 472, 473, 474, 475, 476, 477, 478, 479,
                        497, 498, 499, 500, 501, 502, 503, 504, 505,
                        523, 524, 525, 526, 527, 528, 529, 530, 531,
                        549, 550, 551, 552, 553, 554, 555, 556, 557,
                        575, 576, 577, 578, 579, 580, 581, 582, 583,
                        601, 602, 603, 604, 605, 606, 607, 608, 609);
        for (int i = 0; i < 100000; i++) {
            for (int w = 1; w < 10; w++) {
                final var alu = new Alu(singletonList(w));
                alu.setZRegister(i);
                SECOND_BEFORE_LAST.lines().forEach(alu::executeInstruction);
                if (desiredZValues.contains(alu.zRegister())) {
                    System.out.println("z: " + i + ", w: " + w);
                }
            }
        }
    }

    @Test
    void beforeLast() {
        final var desiredZValues = Set.of(15, 16, 17, 18, 19, 20, 21, 22, 23);
        for (int i = 0; i < 10000; i++) {
            for (int w = 1; w < 10; w++) {
                final var alu = new Alu(singletonList(w));
                alu.setZRegister(i);
                BLOCK_BEFORE_LAST.lines().forEach(alu::executeInstruction);
                if (desiredZValues.contains(alu.zRegister())) {
                    System.out.println("z: " + i + ", w: " + w);
                }
            }
        }
    }

    @Test
    void lastBlock() {
        for (int i = 0; i < 100; i++) {
            for (int w = 1; w < 10; w++) {
                final var alu = new Alu(singletonList(w));
                alu.setZRegister(i);
                LAST_BLOCK.lines().forEach(alu::executeInstruction);
                if (alu.zRegister() == 0) {
                    System.out.println("z: " + i + ", w: " + w);
                }
            }
        }
    }

    @Test
    void trySerialNumbers() {
        var found = false;
        String foundNumber = "";
        final var filteredCandidates = readCandidates().toList();
        final var instructions = readFile().toList();
        for (final String prefix : filteredCandidates) {
            for (long l = 9999999L; !found && l > 0; l--) {
                if (Long.toString(l).contains("0")) {
                    continue;
                }

                final var candidate = prefix + String.format("%07d", l);
                if (candidate.length() < 14) {
                    System.out.println("Candidate " + candidate + " has insufficient lenght!");
                    continue;
                }
                final var input = Arrays.stream(candidate.split(""))
                        .map(Integer::parseInt).toList();

                final var alu = new Alu(input);
                instructions.forEach(alu::executeInstruction);
                found = alu.zRegister() == 0;
                foundNumber = candidate;
            }
            if (found) {
                break;
            }
        }
        assertThat(foundNumber).isEqualTo("0");
    }

    private Stream<String> readCandidates() {
        var inputStream = Day24.class.getResourceAsStream("/day24_after7.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    @Test
    void trySomeNumber() {

        final var alu = new Alu(asList(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
        readFile().forEach(alu::executeInstruction);
        System.out.println();
    }

    @Test
    void trySymbolicExecution() {
        var found = false;
        long foundNumber = 0L;

        final var alu = new Alu(asList(9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9));
        readFile().forEach(alu::executeInstructionSymbolically);
        found = alu.zRegister() == 0;
        assertThat(foundNumber).isEqualTo(0L);
    }

    class Alu {

        private final Map<String, Integer> memory = new HashMap<>();
        private final Map<String, String> symbolicMemory = new HashMap<>();
        private final Deque<Integer> inputs = new ArrayDeque<>();
        private final Deque<String> symbolicInputs = new ArrayDeque<>();

        Alu(final List<Integer> inputs) {
            this.inputs.addAll(inputs);
            IntStream.rangeClosed(1, inputs.size()).mapToObj(i -> "w" + i).forEach(symbolicInputs::add);
            memory.put("w", 0);
            memory.put("x", 0);
            memory.put("y", 0);
            memory.put("z", 0);
            symbolicMemory.put("w", "0");
            symbolicMemory.put("x", "0");
            symbolicMemory.put("y", "0");
            symbolicMemory.put("z", "0");
        }

        Integer valueOfRegister(final String register) {
            return memory.get(register);
        }

        void executeInstruction(final String instruction) {
            final var tokens = instruction.split(" ");
            final var command = tokens[0];
            final var firstRegister = tokens[1];
            final var secondRegisterOrOperand = tokens.length == 3 ? tokens[2] : null;
            final var secondOperand = secondRegisterOrOperand != null && secondRegisterOrOperand.matches("-?\\d+")
                    ? Integer.parseInt(secondRegisterOrOperand)
                    : memory.getOrDefault(secondRegisterOrOperand, 0); // we ignore this in case of default
            switch (command) {
            case "inp":
                memory.replace(firstRegister, inputs.pop());
                break;
            case "add":
                memory.compute(firstRegister, (k, v) -> v + secondOperand);
                break;
            case "mul":
                memory.compute(firstRegister, (k, v) -> v * secondOperand);
                break;
            case "div":
                memory.compute(firstRegister, (k, v) -> v / secondOperand);
                break;
            case "mod":
                memory.compute(firstRegister, (k, v) -> v % secondOperand);
                break;
            case "eql":
                memory.compute(firstRegister, (k, v) -> v == secondOperand ? 1 : 0);
                break;
            }
        }

        void executeInstructionSymbolically(final String instruction) {
            final var tokens = instruction.split(" ");
            final var command = tokens[0];
            final var firstRegister = tokens[1];
            final var secondRegisterOrOperand = tokens.length == 3 ? tokens[2] : null;
            final var secondOperand = secondRegisterOrOperand != null && secondRegisterOrOperand.matches("\\d+")
                    ? secondRegisterOrOperand
                    : symbolicMemory.getOrDefault(secondRegisterOrOperand, "0"); // we ignore this in case of default
            switch (command) {
            case "inp":
                symbolicMemory.replace(firstRegister, symbolicInputs.pop());
                break;
            case "add":
                symbolicMemory.compute(firstRegister,
                        (k, v) -> {
                            if ("0".equals(v)) {
                                return secondOperand;
                            }
                            if ("0".equals(secondOperand)) {
                                return v;
                            }
                            if (v.matches("-?\\d+") && secondOperand.matches("-?\\d+")) {
                                return Integer.toString(Integer.parseInt(v) + Integer.parseInt(secondOperand));
                            }
                            return "(" + v + " + " + secondOperand + ")";
                        });
                break;
            case "mul":
                symbolicMemory.compute(firstRegister, (k, v) -> {
                    if ("0".equals(v) || "0".equals(secondOperand)) {
                        return "0";
                    }
                    if ("1".equals(v)) {
                        return secondOperand;
                    }
                    if ("1".equals(secondOperand)) {
                        return v;
                    }
                    if (v.matches("-?\\d+") && secondOperand.matches("-?\\d+")) {
                        return Integer.toString(Integer.parseInt(v) * Integer.parseInt(secondOperand));
                    }
                    return "(" + v + " * " + secondOperand + ")";
                });
                break;
            case "div":
                symbolicMemory.compute(firstRegister, (k, v) -> {
                    if ("0".equals(v)) {
                        return "0";
                    }
                    if ("1".equals(secondOperand)) {
                        return v;
                    }
                    return "(" + v + " / " + secondOperand + ")";
                });
                break;
            case "mod":
                symbolicMemory.compute(firstRegister, (k, v) -> {
                    if ("0".equals(v)) {
                        return "0";
                    }
                    return "(" + v + " % " + secondOperand + ")";
                });
                break;
            case "eql":
                symbolicMemory.compute(firstRegister, (k, v) -> {
                    if ((v.matches("\\d\\d+") || v.matches(".*\\d\\d+\\)")) && secondOperand.matches("w\\d{1,2}")) {
                        return "0";
                    }
                    if (v.equals(secondOperand)) {
                        return "1";
                    }
                    return "(" + v + " == " + secondOperand + ")";
                });
                break;
            }
        }

        public Integer wRegister() {
            return memory.get("w");
        }

        public Integer xRegister() {
            return memory.get("x");
        }

        public Integer yRegister() {
            return memory.get("y");
        }

        public Integer zRegister() {
            return memory.get("z");
        }

        public String wRegisterSymbolic() {
            return symbolicMemory.get("w");
        }

        public String xRegisterSymbolic() {
            return symbolicMemory.get("x");
        }

        public String yRegisterSymbolic() {
            return symbolicMemory.get("y");
        }

        public String zRegisterSymbolic() {
            return symbolicMemory.get("z");
        }

        public void setZRegister(final int value) {
            memory.put("z", value);
        }
    }
}
