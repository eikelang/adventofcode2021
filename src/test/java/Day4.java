import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class Day4 {

    private Stream<String> readFile() {
        var inputStream = Day4.class.getResourceAsStream("/day4.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    @Test
    void winBingo() {
        final Scanner scanner = new Scanner(Day4.class.getResourceAsStream("/day4.txt"));

        final var calls = scanner.next().split(",");
        scanner.next();

        var cards = new HashSet<BingoCard>();
        scanner.useDelimiter("\\n\\n");
        while (scanner.hasNext()) {
            cards.add(new BingoCard(scanner.next()));
        }

        outer:
        for (final var call : calls) {
            for (final var card : cards) {
                card.call(call);
                if (card.bingo()) {
                    System.out.println("BINGO! " + card.value());
                    assertThat(card.value()).isEqualTo(41503);
                    break outer;
                }
            }
        }
    }

    @Test
    void loseDeliberately() {
        final Scanner scanner = new Scanner(Day4.class.getResourceAsStream("/day4.txt"));

        final var calls = scanner.next().split(",");
        scanner.next();

        var cards = new HashSet<BingoCard>();
        scanner.useDelimiter("\\n\\n");
        while (scanner.hasNext()) {
            cards.add(new BingoCard(scanner.next()));
        }

        outer:
        for (final var call : calls) {
            final Iterator<BingoCard> cardsIterator = cards.iterator();
            while (cardsIterator.hasNext()) {
                final var card = cardsIterator.next();
                card.call(call);
                if (card.bingo()) {
                    cardsIterator.remove();
                    if (cards.isEmpty()) {
                        System.out.println("Last BINGO! " + card.value());
                        assertThat(card.value()).isEqualTo(3178);
                        break outer;
                    }
                }
            }
        }
    }

}
