import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day6 {

    private List<AtomicInteger> readFile() throws IOException {
        var inputStream = Day6.class.getResourceAsStream("/day6.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        var scanner = new Scanner(buf.readLine());
        scanner.useDelimiter(",");
        return scanner.findAll("\\d")
                .map(MatchResult::group)
                .map(s -> new AtomicInteger(Integer.parseInt(s)))
                .collect(Collectors.toList());
    }

    @Test
    void growLanternfishForEightyDays() throws IOException {
        final int fishCount = growLanternFishForDays(80);
        assertThat(fishCount).isEqualTo(390923);
    }

    @Test
    void growLanternfishFor256Days() throws IOException {
        final int fishCount = growLanternFishForDays(128);
        System.out.println(Integer.MAX_VALUE);
        assertThat(fishCount).isEqualTo(390923);
    }

    private int growLanternFishForDays(final int days) throws IOException {
        final List<AtomicInteger> fish = readFile();

        for (int i = 0; i < days; i++) {
            var newFish = 0;
            for (final AtomicInteger daysUntilProcreation : fish) {
                if (daysUntilProcreation.intValue() == 0) {
                    daysUntilProcreation.set(6);
                    newFish++;
                } else {
                    daysUntilProcreation.decrementAndGet();
                }
            }
            for (int j = 0; j < newFish; j++) {
                fish.add(new AtomicInteger(8));
            }
        }
        return fish.size();
    }

}
