import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BingoCardTest {

    private static final String INPUT = """
             3  5  7
             8 10 13
            21 12  9""";

    private static final String EXAMPLE_WINNER = """
            14 21 17 24  4
            10 16 15  9 19
            18  8 23 26 20
            22 11 13  6  5
             2  0 12  3  7""";

    private BingoCard testSubject;

    @BeforeEach
    void setupTestSubject() {
        testSubject = new BingoCard(INPUT);
    }

    @Test
    void initiallyNotBingo() {
        assertThat(testSubject.bingo()).isFalse();
    }

    @Test
    void bingoOnFirstLine() {
        Stream.of("3", "5", "7").forEach(testSubject::call);
        assertThat(testSubject.bingo()).isTrue();
    }

    @Test
    void bingoOnLastLine() {
        Stream.of("3", "7", "12", "10", "9", "21").forEach(testSubject::call);
        assertThat(testSubject.bingo()).isTrue();
    }

    @Test
    void bingoOnFirstColumn() {
        Stream.of("3", "7", "12", "10", "21", "8").forEach(testSubject::call);
        assertThat(testSubject.bingo()).isTrue();
    }

    @Test
    void unmarkedSumUnplayedCard() {
        assertThat(testSubject.unmarkedSum()).isEqualTo(88);
    }

    @Test
    void unmarkedSumAfterFirstColumnBingo() {
        Stream.of("3", "7", "12", "10", "21", "8").forEach(testSubject::call);
        assertThat(testSubject.unmarkedSum()).isEqualTo(27);
    }

    @Test
    void exampleFromAssignment() {
        final var winningCard = new BingoCard(EXAMPLE_WINNER);
        Stream.of("7", "4", "9", "5", "11", "17", "23", "2", "0", "14", "21", "24").forEach(winningCard::call);
        assertThat(winningCard.unmarkedSum()).isEqualTo(188);
        assertThat(winningCard.bingo()).isTrue();
    }
}
