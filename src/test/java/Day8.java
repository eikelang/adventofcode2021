import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day8 {

    private static final String SMALL_EXAMPLE = """
            be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
            edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
            fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
            fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
            aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
            fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
            dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
            bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
            egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
            gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce""";

    private Stream<String> readFile() {
        var inputStream = Day8.class.getResourceAsStream("/day8.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    @Test
    void countEasyDigits() {
        assertThat(
                readFile()
                        .mapToLong(this::countKnownDigits)
                        .sum()
        ).isEqualTo(493L);
    }

    @Test
    void countEasyDigitsSmall() {
        assertThat(
                SMALL_EXAMPLE.lines()
                        .mapToLong(this::countKnownDigits)
                        .sum()
        ).isEqualTo(26L);
    }

    @Test
    void fullDecodeSingleLine() {
        final String[] parts =
                "acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab | cdfeb fcadb cdfeb cdbaf".split("\\|");
        final Map<Set<Character>, Integer> decoderMap = createDecoder(parts[0]);

        final int result = decodeLine(parts[1], decoderMap);
        assertThat(result).isEqualTo(5353);
    }

    @Test
    void fullDecodeFirstLine() {
        final String[] parts =
        "daegb gadbcf cgefda edcfagb dfg acefbd fdgab fg bdcfa fcgb | cdfgba fgbc dbfac gfadbc".split("\\|");

        final Map<Set<Character>, Integer> decoderMap = createDecoder(parts[0]);

        final int result = decodeLine(parts[1], decoderMap);
        assertThat(result).isEqualTo(9459);
    }

    @Test
    void fullDecode() {
        final int sum = readFile().mapToInt(l -> {
            final String[] parts = l.split("\\|");
            final Map<Set<Character>, Integer> decoderMap = createDecoder(parts[0]);

            return decodeLine(parts[1], decoderMap);
        }).sum();
        assertThat(sum).isEqualTo(1010460);
    }

    private int decodeLine(final String part, final Map<Set<Character>, Integer> decoderMap) {
        String intermediateResult = "";
        for (String s : part.trim().split(" ")) {
            final Set<Character> characters = stringToChars(s);
            final Integer integer = decoderMap.get(characters);
            intermediateResult += integer;
        }

        return Integer.parseInt(intermediateResult);
    }

    private Map<Set<Character>, Integer> createDecoder(final String signalString) {
        final String[] signalStream = signalString.split(" ");
        final Map<Integer, List<Set<Character>>> signalsBySize =
                Arrays.stream(signalStream).map(this::stringToChars).collect(Collectors.groupingBy(Set::size));
        final Set<Character> oneSignals = signalsBySize.get(2).get(0);
        final Set<Character> sevenSignals = signalsBySize.get(3).get(0);
        final Set<Character> fourSignals = signalsBySize.get(4).get(0);
        final Set<Character> eightSignals = signalsBySize.get(7).get(0);
        final Set<Character> nineSignals = signalsBySize.get(6).stream()
                .filter(set -> set.containsAll(fourSignals))
                .findFirst()
                .get();
        final Set<Character> zeroSignals = signalsBySize.get(6).stream()
                .filter(set -> !set.containsAll(nineSignals))
                .filter(set -> set.containsAll(oneSignals))
                .findFirst()
                .get();
        final Set<Character> sixSignals = signalsBySize.get(6).stream()
                .filter(set -> !set.containsAll(zeroSignals))
                .filter(set -> !set.containsAll(nineSignals))
                .findFirst().get();

        final Set<Character> threeSignals =
                signalsBySize.get(5).stream().filter(set -> set.containsAll(sevenSignals)).findFirst().get();
        final Set<Character> fiveSignals =
                signalsBySize.get(5).stream()
                        .filter(set -> !set.containsAll(threeSignals))
                        .filter(set -> nineSignals.containsAll(set)).findFirst().get();
        final Set<Character> twoSignals = signalsBySize.get(5).stream()
                .filter(set -> !set.containsAll(fiveSignals))
                .filter(set -> !set.containsAll(threeSignals))
                .findFirst().get();
        final Map<Set<Character>, Integer> decode = new HashMap<>();
        decode.put(zeroSignals, 0);
        decode.put(oneSignals, 1);
        decode.put(twoSignals, 2);
        decode.put(threeSignals, 3);
        decode.put(fourSignals, 4);
        decode.put(fiveSignals, 5);
        decode.put(sixSignals, 6);
        decode.put(sevenSignals, 7);
        decode.put(eightSignals, 8);
        decode.put(nineSignals, 9);
        return decode;
    }

    private long countKnownDigits(final String inputLine) {
        return Arrays.stream(inputLine.split("\\|")[1].split(" "))
                .filter(s -> (s.length() >= 2 && s.length() <= 4) || s.length() == 7)
                .count();
    }

    private Set<Character> stringToChars(final String input) {
        final var chars = new HashSet<Character>();
        for (char c : input.toCharArray()) {
            chars.add(c);
        }
        return chars;
    }

}
