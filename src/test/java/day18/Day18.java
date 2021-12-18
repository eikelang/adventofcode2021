package day18;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day18 {

    private static final String BIGGER_EXAMPLE = """
            [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]
            [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]
            [[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]
            [[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]
            [7,[5,[[3,8],[1,4]]]]
            [[2,[2,2]],[8,[8,1]]]
            [2,9]
            [1,[[[9,3],9],[[9,0],[0,7]]]]
            [[[5,[7,4]],7],1]
            [[[[4,2],2],6],[8,7]]""";

    private Stream<String> readFile() {
        var inputStream = Day18.class.getResourceAsStream("/day18.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    @Test
    void parseSimpleNumber() {
        final SnailfishNumber number = parse("[1,1]");
        assertThat(number.isRegular()).isFalse();
        assertThat(number.left().isRegular()).isTrue();
        assertThat(number.left().value()).isEqualTo(1);
        assertThat(number.right().isRegular()).isTrue();
        assertThat(number.right().value()).isEqualTo(1);
    }

    @Test
    void parseExampleFromFile() {
        final SnailfishNumber number = parse("[[2,[[3,8],6]],[[[3,2],[1,4]],[[1,6],[9,4]]]]");
        assertThat(number).hasToString("[[2,[[3,8],6]],[[[3,2],[1,4]],[[1,6],[9,4]]]]");
    }

    @Test
    void simpleAddition() {
        final SnailfishNumber leftOperand = parse("[1,1]");
        final SnailfishNumber rightOperand = parse("[2,2]");
        assertThat(leftOperand.plus(rightOperand)).hasToString("[[1,1],[2,2]]");
    }

    @Test
    void additionWithoutReduction() {
        final SnailfishNumber firstOperand = parse("[1,1]");
        final SnailfishNumber secondOperand = parse("[2,2]");
        final SnailfishNumber thirdOperand = parse("[3,3]");
        final SnailfishNumber fourthOperand = parse("[4,4]");

        final Optional<SnailfishNumber> sum = Stream.of(firstOperand, secondOperand, thirdOperand, fourthOperand)
                .reduce(SnailfishNumber::plus);
        assertThat(sum.map(Object::toString)).contains("[[[[1,1],[2,2]],[3,3]],[4,4]]");
    }

    @Test
    void mustReduceDueToDepth() {
        final SnailfishNumber sumSoFar = parse("[[[[1,1],[2,2]],[3,3]],[4,4]]");
        final var result = sumSoFar.plus(parse("[5,5]"));
        assertThat(result.mustReduce(0)).isTrue();
    }

    @Test
    void mustReduceDueToNumber() {
        final SnailfishNumber number = new Pair(new Regular(10), new Regular(5));
        assertThat(number.mustReduce(0)).isTrue();
    }

    @Test
    void detectExplosion() {
        final SnailfishNumber number = parse("[[[[[9,8],1],2],3],4]");
        final var explosion = number.findFirstExplosion(0);
        assertThat(explosion.map(Object::toString)).contains("[9,8]");
    }

    @Test
    void removeExplodedPair() {
        final SnailfishNumber number = parse("[[[[[9,8],1],2],3],4]");
        final var explosion = number.findFirstExplosion(0);
        number.resolveExplosion(explosion.get());
        assertThat(number).hasToString("[[[[0,9],2],3],4]");
    }

    @Test
    void calculateMagnitude() {
        final SnailfishNumber number = parse("[[1,2],[[3,4],5]]");
        assertThat(number.magnitude()).isEqualTo(143);
    }

    @Test
    void magnitudeOfBiggerExample() {
        final var parse = parse("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]");
        assertThat(parse.magnitude()).isEqualTo(3488L);
    }

    @Test
    void firstAdditionOfBiggerExample() {
        final var snailfishNumbers = BIGGER_EXAMPLE.lines().limit(2).map(this::parse)
                .reduce(SnailfishNumber::plus).map(Objects::toString);
        System.out.println(BIGGER_EXAMPLE.lines().limit(2).toList());
        assertThat(snailfishNumbers.map(String::toString)).contains(
                "[[[[4,0],[5,4]],[[7,7],[6,0]]],[[8,[7,7]],[[7,9],[5,0]]]]");
    }

    @Test
    void sumOfBiggerExample() {
        final var snailfishNumbers = BIGGER_EXAMPLE.lines().map(this::parse)
                .reduce(SnailfishNumber::plus).map(Objects::toString);
        assertThat(snailfishNumbers.map(String::toString)).contains(
                "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]");
    }

    @Test
    void noSplitLeavesNumberUnchanged() {
        final var toSplit = parse("[[1,2],[[3,4],5]]");
        assertThat(toSplit.splitFirst()).hasToString("[[1,2],[[3,4],5]]");
    }

    @Test
    void trySplit() {
        final var toSplit = parse("[[[[0,7],4],[15,[0,13]]],[1,1]]");
        assertThat(toSplit.splitFirst()).hasToString("[[[[0,7],4],[[7,8],[0,13]]],[1,1]]");
    }

    @Test
    void tryReduce() {
        final var parse = parse("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]");
        final var manualResult = parse.explode().explode().splitFirst().splitFirst().explode();
        assertThat(manualResult).hasToString("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]");
    }

    @Test
    void tryReduceAutomatically() {
        final var parse = parse("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]");
        final var automaticResult = parse.reduce();
        assertThat(automaticResult).hasToString("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]");
    }

    @Test
    void tryAll() {
        final var sum = readFile().map(this::parse).reduce(SnailfishNumber::plus);
        assertThat(sum.map(SnailfishNumber::magnitude)).contains(0L);
    }

    @Test
    void largestSum() {
        final var strings = readFile().toList();
        var max = 0L;
        for (int i = 0; i< strings.size(); i++) {
            for (int j = 0; j<strings.size(); j++) {
                max = Math.max(max, parse(strings.get(i)).plus(parse(strings.get(j))).magnitude());
            }
        }
        assertThat(max).isEqualTo(4616L);
    }

    interface SnailfishNumber {

        SnailfishNumber reduce();

        boolean isRegular();

        SnailfishNumber left();

        SnailfishNumber right();

        int value();

        SnailfishNumber plus(SnailfishNumber rightOperand);

        boolean mustReduce(final int parentDepth);

        Optional<SnailfishNumber> findFirstExplosion(int parentDepth);

        long magnitude();

        void resolveExplosion(SnailfishNumber explosion);

        Stream<SnailfishNumber> valuesUntil(final SnailfishNumber endNode);

        Stream<SnailfishNumber> valuesAfter(SnailfishNumber startNode);

        void visitWith(Consumer<SnailfishNumber> visitor);

        void replaceExposion(SnailfishNumber explosion);

        SnailfishNumber splitFirst();

        boolean needsSplit();

        SnailfishNumber explode();
    }

    class Pair implements SnailfishNumber {

        private SnailfishNumber left;
        private SnailfishNumber right;

        public Pair(final SnailfishNumber left, final SnailfishNumber right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public SnailfishNumber reduce() {
            boolean exploded = false;
            boolean splitOccured = false;
            do {
                exploded = false;
                splitOccured = false;
                final var firstExplosion = findFirstExplosion(0);
                if (firstExplosion.isPresent()) {
                    exploded = true;
                    this.resolveExplosion(firstExplosion.get());
                    this.replaceExposion(firstExplosion.get());
                    continue;
                }
                splitOccured = this.needsSplit();
                this.splitFirst();

            } while (exploded || splitOccured);
            return this;
        }

        @Override
        public boolean isRegular() {
            return false;
        }

        @Override
        public SnailfishNumber left() {
            return left;
        }

        @Override
        public SnailfishNumber right() {
            return right;
        }

        @Override
        public int value() {
            return 0;
        }

        @Override
        public SnailfishNumber plus(final SnailfishNumber rightOperand) {
            return new Pair(this, rightOperand).reduce();
        }

        @Override
        public boolean mustReduce(final int parentDepth) {
            return parentDepth >= 4 || left.mustReduce(parentDepth + 1) || right.mustReduce(parentDepth + 1);
        }

        @Override
        public Optional<SnailfishNumber> findFirstExplosion(final int parentDepth) {
            if (parentDepth >= 4) {
                return Optional.of(this);
            }
            return Stream.of(left.findFirstExplosion(parentDepth + 1), right.findFirstExplosion(parentDepth + 1))
                    .filter(Optional::isPresent)
                    .findFirst()
                    .flatMap(Function.identity());
        }

        @Override
        public long magnitude() {
            return 3 * left.magnitude() + 2 * right.magnitude();
        }

        @Override
        public void resolveExplosion(final SnailfishNumber explosion) {
            final PrefixFindingVisitor prefixFinder = new PrefixFindingVisitor(explosion);
            final PostfixFindingVisitor postfixFinder = new PostfixFindingVisitor(explosion);
            this.visitWith(prefixFinder);
            this.visitWith(postfixFinder);
            prefixFinder.before().ifPresent(r -> r.increaseBy(explosion.left().value()));
            postfixFinder.after().ifPresent(r -> r.increaseBy(explosion.right().value()));
            if (left == explosion) {
                left = new Regular(0);
            } else if (right == explosion) {
                right = new Regular(0);
            } else {
                left.replaceExposion(explosion);
                right.replaceExposion(explosion);
            }
        }

        @Override
        public Stream<SnailfishNumber> valuesUntil(final SnailfishNumber endNode) {
            if (left == endNode) {
                return Stream.empty();
            }
            if (right == endNode) {
                return left.valuesUntil(endNode);
            } else {
                return Stream.concat(left.valuesUntil(endNode), right.valuesUntil(endNode));
            }
        }

        @Override
        public Stream<SnailfishNumber> valuesAfter(final SnailfishNumber startNode) {
            if (right == startNode) {
                return Stream.empty();
            }
            if (left == startNode) {
                return right.valuesAfter(startNode);
            } else {
                return Stream.concat(left.valuesUntil(startNode), right.valuesUntil(startNode));
            }
        }

        @Override
        public void visitWith(final Consumer<SnailfishNumber> visitor) {
            visitor.accept(this);
            left.visitWith(visitor);
            right.visitWith(visitor);
        }

        @Override
        public void replaceExposion(final SnailfishNumber explosion) {
            if (left == explosion) {
                left = new Regular(0);
            } else if (right == explosion) {
                right = new Regular(0);
            } else {
                left.replaceExposion(explosion);
                right.replaceExposion(explosion);
            }
        }

        @Override
        public SnailfishNumber splitFirst() {
            if (left.needsSplit()) {
                this.left = left.splitFirst();
            } else if (right.needsSplit()) {
                this.right = right.splitFirst();
            }
            return this;
        }

        @Override
        public boolean needsSplit() {
            return left.needsSplit() || right.needsSplit();
        }

        @Override
        public SnailfishNumber explode() {
            final var firstExplosion = this.findFirstExplosion(0);
            firstExplosion.ifPresent(e -> {
                this.resolveExplosion(e);
                this.replaceExposion(e);
            });
            return this;
        }

        @Override
        public String toString() {
            return "[" + left + "," + right + "]";
        }
    }

    class Regular implements SnailfishNumber {

        int number;

        Regular(final int number) {
            this.number = number;
        }

        public SnailfishNumber splitFirst() {
            if (needsSplit()) {
                return new Pair(new Regular(number / 2), new Regular(number / 2 + number % 2));
            }
            return this;
        }

        @Override
        public boolean needsSplit() {
            return number > 9;
        }

        @Override
        public SnailfishNumber explode() {
            return this;
        }

        @Override
        public SnailfishNumber reduce() {
            return this;
        }

        @Override
        public boolean isRegular() {
            return true;
        }

        @Override
        public SnailfishNumber left() {
            return null;
        }

        @Override
        public SnailfishNumber right() {
            return null;
        }

        @Override
        public int value() {
            return number;
        }

        @Override
        public SnailfishNumber plus(final SnailfishNumber rightOperand) {
            return new Pair(this, rightOperand);
        }

        @Override
        public boolean mustReduce(final int parentDepth) {
            return number > 0;
        }

        @Override
        public Optional<SnailfishNumber> findFirstExplosion(final int parentDepth) {
            return Optional.empty();
        }

        @Override
        public long magnitude() {
            return value();
        }

        @Override
        public void resolveExplosion(final SnailfishNumber explosion) {
            // do nothing, Regulars don't explode
        }

        @Override
        public Stream<SnailfishNumber> valuesUntil(final SnailfishNumber endNode) {
            return Stream.of(this);
        }

        @Override
        public Stream<SnailfishNumber> valuesAfter(final SnailfishNumber startNode) {
            return Stream.of(this);
        }

        @Override
        public void visitWith(final Consumer<SnailfishNumber> visitor) {
            visitor.accept(this);
        }

        @Override
        public void replaceExposion(final SnailfishNumber explosion) {

        }

        @Override
        public String toString() {
            return Integer.toString(number);
        }

        public void increaseBy(final int increment) {
            this.number += increment;
        }
    }

    SnailfishNumber parse(final String input) {
        final var inputStack = new ArrayDeque<>(input.chars().boxed().toList());
        final Deque<SnailfishNumber> output = new ArrayDeque<>();
        while (!inputStack.isEmpty()) {
            var item = inputStack.pop();
            switch (item.intValue()) {
            case '[':
                break;
            case ',':
                break;
            case ']':
                var right = output.pop();
                var left = output.pop();
                output.push(new Pair(left, right));
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                int value = toDigit(item);
                while (isDigit(inputStack.peek())) {
                    value = value * 10 + (toDigit(inputStack.pop()));
                }
                output.push(new Regular(value));
                break;
            default:
                throw new RuntimeException();

            }
        }
        return output.pop();
    }

    private int toDigit(final Integer item) {
        return item - '0';
    }

    private boolean isDigit(final Integer value) {
        return '0' <= value && value <= '9';
    }

    static class Explosion {

        private final int left;
        private final int right;

        Explosion(final int left, final int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Explosion explosion = (Explosion) o;
            return left == explosion.left && right == explosion.right;
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }
    }

    private static class PrefixFindingVisitor implements Consumer<SnailfishNumber> {

        private Deque<Regular> valueCollector = new ArrayDeque<>();
        private boolean limitReached = false;
        private SnailfishNumber stopPoint;

        public PrefixFindingVisitor(final SnailfishNumber stopPoint) {
            this.stopPoint = stopPoint;
        }

        @Override
        public void accept(final SnailfishNumber snailfishNumber) {
            if (!limitReached && snailfishNumber instanceof Regular r) {
                valueCollector.push(r);
            }
            if (!limitReached && stopPoint == snailfishNumber) {
                limitReached = true;
            }
        }

        Optional<Regular> before() {
            return valueCollector.isEmpty() ? Optional.empty() : Optional.of(valueCollector.pop());
        }
    }

    private static class PostfixFindingVisitor implements Consumer<SnailfishNumber> {

        private Regular value = null;
        private boolean startPointReached = false;
        private SnailfishNumber startPoint;
        private int toSkip = 2; // due to visiting order, next two numbers are the exploding pair

        public PostfixFindingVisitor(final SnailfishNumber startPoint) {
            this.startPoint = startPoint;
        }

        @Override
        public void accept(final SnailfishNumber snailfishNumber) {
            if (startPoint == snailfishNumber) {
                startPointReached = true;
            }
            if (startPointReached && value == null) {
                if (snailfishNumber instanceof Regular r) {
                    if (toSkip > 0) {
                        toSkip--;
                    } else {
                        value = r;
                    }
                }
            }
        }

        Optional<Regular> after() {
            return Optional.ofNullable(value);
        }
    }

}
