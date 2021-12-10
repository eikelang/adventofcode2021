package day10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParseTest {

    @Test
    @DisplayName("A single opening paren is an incomplete parse.")
    void incomplete() {
        assertThat(new Parse("{").isIncomplete()).isTrue();
    }

    @Test
    @DisplayName("An incomplete parse is not corrupt.")
    void incompleteNotCorrupt() {
        assertThat(new Parse("{<").isCorrupt()).isFalse();
    }

    @Test
    @DisplayName("A mismatched paren pair is corrupt.")
    void corrupt() {
        assertThat(new Parse("{]").isCorrupt()).isTrue();
    }

    @Test
    @DisplayName("A corrupt parse is not incomplete.")
    void corruptNotIncomplete() {
        assertThat(new Parse("{]").isIncomplete()).isFalse();
    }

    @Test
    @DisplayName("The line '[(()[<>])]({[<{<<[]>>(' is incomplete")
    void incompleteFromSample() {
        assertThat(new Parse("[(()[<>])]({[<{<<[]>>(").isIncomplete()).isTrue();
    }

    @Test
    @DisplayName("The line '{([(<{}[<>[]}>{[]{[(<()>' is corrupt")
    void corruptFromSample() {
        assertThat(new Parse("{([(<{}[<>[]}>{[]{[(<()>").isCorrupt()).isTrue();
    }

    @Test
    @DisplayName("The syntax error score for an illegal angle bracket is 25137 points")
    void syntaxScoreForWrongAngle() {
        assertThat(new Parse("<{([([[(<>()){}]>(<<{{").syntaxErrorScore()).isEqualTo(25137);
    }

    @Test
    @DisplayName("The completion score for '<{([{{}}[<[[[<>{}]]]>[]]' is 294")
    void completionScoreForLastSampleLine() {
        assertThat(new Parse("<{([{{}}[<[[[<>{}]]]>[]]").completionScore()).isEqualTo(294);
    }
}