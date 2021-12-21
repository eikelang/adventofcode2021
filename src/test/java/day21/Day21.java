package day21;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class Day21 {

    private static final int PLAYER1_START = 2;
    private static final int PLAYER2_START = 10;

    private static final Map<Integer, Integer> QUANTUM_ROLLS_WITH_FREQUENCY = new HashMap<>();

    static {
        QUANTUM_ROLLS_WITH_FREQUENCY.put(3, 1);
        QUANTUM_ROLLS_WITH_FREQUENCY.put(4, 3);
        QUANTUM_ROLLS_WITH_FREQUENCY.put(5, 6);
        QUANTUM_ROLLS_WITH_FREQUENCY.put(6, 7);
        QUANTUM_ROLLS_WITH_FREQUENCY.put(7, 6);
        QUANTUM_ROLLS_WITH_FREQUENCY.put(8, 3);
        QUANTUM_ROLLS_WITH_FREQUENCY.put(9, 1);
    }

    @Test
    void testDieBehaviour() {
        final var deterministicDie = new DeterministicDie();
        final var streamOfThrows = IntStream.generate(deterministicDie).limit(5);
        assertThat(streamOfThrows).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    void testDieRollover() {
        final var deterministicDie = new DeterministicDie();
        final var streamOfThrows = IntStream.generate(deterministicDie).skip(99).limit(5);
        assertThat(streamOfThrows).containsExactly(100, 1, 2, 3, 4);
        assertThat(deterministicDie.totalRolls).isEqualTo(104);
    }

    @Test
    void testMovement() {
        final var deterministicDie = new DeterministicDie();
        final var mockedPawn = mock(Pawn.class);
        makeMove(mockedPawn, deterministicDie);
        verify(mockedPawn).moveBy(6);
    }

    @Test
    void testMovementWithRollover() {
        final var pawn = new Pawn(4);
        pawn.moveBy(20);
        assertThat(pawn.position).isEqualTo(4);
    }

    @Test
    void testMovementWithRolloverToPositionDivisibleBy10() {
        final var pawn = new Pawn(4);
        pawn.moveBy(16);
        assertThat(pawn.position).isEqualTo(10);
    }

    @Test
    void testPawnPosition() {
        final var pawn = new Pawn(9);
        pawn.moveBy(2);
        assertThat(pawn.position).isEqualTo(1);
    }

    @Test
    void testPawnScore() {
        final var pawn = new Pawn(9);
        pawn.moveBy(5);
        assertThat(pawn.score).isEqualTo(4);
    }

    @Test
    void playFirstExample() {
        final var die = new DeterministicDie();
        final var player1 = new Pawn(4);
        final var player2 = new Pawn(8);
        final var won = makeMove(player1, die);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(won).isFalse();
            softly.assertThat(player1.position).isEqualTo(10);
            softly.assertThat(player1.score).isEqualTo(10);
        });
    }

    @Test
    void playLongerExample() {
        final var die = new DeterministicDie();
        final var player1 = new Pawn(4);
        final var player2 = new Pawn(8);
        makeMove(player1, die);
        makeMove(player2, die);
        makeMove(player1, die);
        makeMove(player2, die);
        makeMove(player1, die);
        makeMove(player2, die);
        makeMove(player1, die);
        makeMove(player2, die);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(player1.position).isEqualTo(6);
            softly.assertThat(player1.score).isEqualTo(26);
            softly.assertThat(player2.position).isEqualTo(6);
            softly.assertThat(player2.score).isEqualTo(22);
        });
    }

    @Test
    void replayExample() {
        final var die = new DeterministicDie();
        final var player1 = new Pawn(4);
        final var player2 = new Pawn(8);

        boolean eitherHasWon = false;
        while (!eitherHasWon) {
            eitherHasWon = makeMove(player1, die);
            if (!eitherHasWon) {
                eitherHasWon = makeMove(player2, die);
            }
        }

        var result = player2.score * die.totalRolls;
        assertThat(result).isEqualTo(739785);
    }

    @Test
    void playUntilWin() {
        final var die = new DeterministicDie();
        final var player1 = new Pawn(PLAYER1_START);
        final var player2 = new Pawn(PLAYER2_START);
        final var playerGenerator = new Supplier<Pawn>() {

            private Pawn next = player1;

            @Override
            public Pawn get() {
                final var toReturn = next;
                next = next == player1 ? player2 : player1;
                return toReturn;
            }
        };
        final var allMoves = Stream.generate(playerGenerator).map(p -> makeMove(p, die)).takeWhile(r -> !r).collect(
                Collectors.toList());
        var result = 0;
        if (player1.hasWon(1000)) {
            result = player2.score * die.totalRolls;
        } else {
            result = player1.score * die.totalRolls;
        }
        assertThat(result).isEqualTo(571032);
    }

    @Test
    void playQuantumDice() {
        final var player1 = new Pawn(PLAYER1_START, "Player1");
        final var player2 = new Pawn(PLAYER2_START, "Player2");
        final var totalWinnerCounter = new HashMap<String, Long>();
        QUANTUM_ROLLS_WITH_FREQUENCY.forEach((roll, frequency) -> {
            final var winnersForThrow =
                    new DiceGame(player1.copy(), player2.copy(), roll).winnersByName();
            winnersForThrow.forEach((key, value) -> totalWinnerCounter.merge(key, frequency * value, Long::sum));
        });
        final var mostWins = Math.max(totalWinnerCounter.get("Player1"), totalWinnerCounter.get("Player2"));
        assertThat(mostWins).isEqualTo(49975322685009L);
    }

    @Test
    void playQuantumDiceForExample() {
        final var player1 = new Pawn(4, "Player1");
        final var player2 = new Pawn(8, "Player2");
        final var totalWinnerCounter = new HashMap<String, Long>();
        QUANTUM_ROLLS_WITH_FREQUENCY.forEach((roll, frequency) -> {
            final var winnersForThrow =
                    new DiceGame(player1.copy(), player2.copy(), roll).winnersByName();
            winnersForThrow.forEach((key, value) -> totalWinnerCounter.merge(key, frequency * value, Long::sum));
        });
        final var player1Wins = totalWinnerCounter.get("Player1");
        final var player2Wins = totalWinnerCounter.get("Player2");
        final var mostWins = Math.max(player1Wins, player2Wins);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(mostWins).isEqualTo(444356092776315L);
            softly.assertThat(player2Wins).isEqualTo(341960390180808L);
        });
    }

    private boolean makeMove(final Pawn pawn, final DeterministicDie die) {
        final var dieResult = IntStream.generate(die).limit(3).sum();
        pawn.moveBy(dieResult);
        return pawn.hasWon(1000);
    }

    class Pawn {

        private int position;
        private int score;
        private String name;

        public Pawn(final int startingPosition) {
            position = startingPosition;
        }

        public Pawn(final int startingPosition, final String name) {
            position = startingPosition;
            this.name = name;
        }

        public void moveBy(final int segments) {
            final var intermediatePosition = position + (segments % 10);
            if (intermediatePosition > 10) {
                position = intermediatePosition % 10;
            } else {
                position = intermediatePosition;
            }
            score += position;
        }

        public boolean hasWon(final int winningScore) {
            return score >= winningScore;
        }

        public Pawn copy() {
            final var copy = new Pawn(position, name);
            copy.score = score;
            return copy;
        }
    }

    class DeterministicDie implements IntSupplier {

        private int totalRolls = 0;
        private int nextThrow = 1;

        @Override
        public int getAsInt() {
            final var toReturn = nextThrow;
            nextThrow++;
            nextThrow = nextThrow > 100 ? nextThrow % 100 : nextThrow;
            totalRolls++;
            return toReturn;
        }
    }

    class DiceGame {

        private final Pawn playerWhoIsUp;
        private final Pawn nextPlayer;

        private DiceGame(final Pawn playerWhoIsUp, final Pawn nextPlayer, final int dieSum) {
            this.playerWhoIsUp = playerWhoIsUp;
            this.nextPlayer = nextPlayer;
            this.playerWhoIsUp.moveBy(dieSum);
        }

        Map<String, Long> winnersByName() {
            final var winnerMap = new HashMap<String, Long>();
            if (playerWhoIsUp.hasWon(21)) {
                winnerMap.put(playerWhoIsUp.name, 1L);
            } else if (nextPlayer.hasWon(21)) {
                winnerMap.put(nextPlayer.name, 1L);
            } else {
                QUANTUM_ROLLS_WITH_FREQUENCY.forEach((roll, frequency) -> {
                    final var winnersForThrow =
                            new DiceGame(nextPlayer.copy(), playerWhoIsUp.copy(), roll).winnersByName();
                    winnersForThrow.forEach((key, value) -> winnerMap.merge(key, frequency * value, Long::sum));
                });
            }
            return winnerMap;
        }
    }

}
