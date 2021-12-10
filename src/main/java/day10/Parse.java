package day10;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

class Parse {

    private final List<ParseToken> corruptTokens;
    private final Deque<ParseToken> parseStack = new ArrayDeque<>();

    Parse(final CharSequence input) {
        corruptTokens = input.chars().mapToObj(ParseToken::fromCharacter)
                .dropWhile(t -> {
                    if (t.isOpener()) {
                        parseStack.push(t);
                        return true;
                    } else {
                        return t.closes(parseStack.pop());
                    }
                }).toList();
    }

    boolean isIncomplete() {
        return corruptTokens.isEmpty();
    }

    boolean isCorrupt() {
        return !corruptTokens.isEmpty();
    }

    long syntaxErrorScore() {
        return corruptTokens.stream().findFirst().map(ParseToken::invalidScore).orElse(0);
    }

    long completionScore() {
        return parseStack.stream()
                .mapToLong(ParseToken::completionScore)
                .reduce(0, (acc, l) -> acc * 5 + l);
    }

    private enum ParseToken {
        LPAR(true, 0, 1, '('),
        RPAR(false, 3, 0, ')') {
            @Override
            boolean closes(final ParseToken toClose) {
                return toClose == LPAR;
            }
        },
        LBRAK(true, 0, 2, '['),
        RBRAK(false, 57, 0, ']') {
            @Override
            boolean closes(final ParseToken toClose) {
                return toClose == LBRAK;
            }
        },
        LCURL(true, 0, 3,  '{'),
        RCURL(false, 1197, 0,  '}') {
            @Override
            boolean closes(final ParseToken toClose) {
                return toClose == LCURL;
            }
        },
        LANGLE(true, 0, 4, '<'),
        RANGLE(false, 25137, 0, '>') {
            @Override
            boolean closes(final ParseToken toClose) {
                return toClose == LANGLE;
            }
        };

        private final boolean opener;
        private final int illegalScore;
        private final int completionScore;
        private final char character;

        ParseToken(final boolean opener, final int illegalScore, final int completionScore, final char character) {
            this.opener = opener;
            this.illegalScore = illegalScore;
            this.completionScore = completionScore;
            this.character = character;
        }

        boolean isOpener() {
            return opener;
        }

        boolean closes(final ParseToken toClose) {
            return false;
        }

        int invalidScore() {
            return illegalScore;
        }

        int completionScore() {
            return completionScore;
        }

        static ParseToken fromCharacter(final int input) {
            return Arrays.stream(ParseToken.values()).filter(t -> t.character == input)
                    .findFirst().orElseThrow(RuntimeException::new);
        }

        @Override
        public String toString() {
            return Character.toString(character);
        }
    }
}
