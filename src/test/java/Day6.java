import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day6 {

    private Map<Integer, Long> readFile() throws IOException {
        var inputStream = Day6.class.getResourceAsStream("/day6.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        var scanner = new Scanner(buf.readLine());
        scanner.useDelimiter(",");
        return scanner.findAll("\\d")
                .map(MatchResult::group)
                .map(Integer::parseInt)
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()));
    }

    @Test
    void growLanternfishForEightyDays() throws IOException {
        final long fishCount = growLanternFishForDays(80);
        assertThat(fishCount).isEqualTo(390923);
    }

    @Test
    void growLanternfishFor256Days() throws IOException {
        final long fishCount = growLanternFishForDays(256);
        assertThat(fishCount).isEqualTo(1749945484935L);
    }

    private long growLanternFishForDays(final int days) throws IOException {
        final Map<Integer, Long> fish = readFile();
        for (int i = 0; i <= 8; i++) {
            fish.putIfAbsent(i, 0L);
        }

        for (int i = 0; i < days; i++) {
            final var newFish = fish.getOrDefault(0, 0L);
            for (int j = 0; j < 8; j++) {
                fish.put(j, fish.get(j + 1));
            }
            fish.put(6, fish.get(6) + newFish);
            fish.put(8, newFish);
        }
        return fish.values().stream().mapToLong(l -> l).sum();
    }

}
