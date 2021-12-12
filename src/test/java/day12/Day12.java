package day12;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class Day12 {

    private static final String EXAMPLE = """
            start-A
            start-b
            A-c
            A-b
            b-d
            A-end
            b-end""";

    private static final String LARGE_SAMPLE = """
            dc-end
            HN-start
            start-kj
            dc-start
            dc-HN
            LN-dc
            HN-end
            kj-sa
            kj-HN
            kj-dc""";

    private Stream<String> readFile() {
        var inputStream = Day12.class.getResourceAsStream("/day12.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines();
    }

    private Stream<String> readExample(final String example) {
        return example.lines();
    }

    @Test
    void pathsToMap() {
        final Map<String, Set<String>> passagesByStart = readExample(EXAMPLE)
                .map(s -> s.split("-"))
                .flatMap(a -> Stream.of(new CavePassage(a[0], a[1]), new CavePassage(a[1], a[0])))
                .collect(Collectors.groupingBy(cp -> cp.from, Collectors.mapping(cp -> cp.to, Collectors.toSet())));
        passagesByStart.values().forEach(v -> v.remove("start"));
        final List<List<String>> result2 =
                continuePath(singletonList("start"), passagesByStart, false)
                        .filter(list -> "end".equals(list.get(list.size() - 1)))
                        .collect(Collectors.toList());
        System.out.println(result2);
        assertThat(result2).hasSize(10);
    }

    @Test
    void pathsToMapWithRevisit() {
        final Map<String, Set<String>> passagesByStart = readExample(EXAMPLE)
                .map(s -> s.split("-"))
                .flatMap(a -> Stream.of(new CavePassage(a[0], a[1]), new CavePassage(a[1], a[0])))
                .collect(Collectors.groupingBy(cp -> cp.from, Collectors.mapping(cp -> cp.to, Collectors.toSet())));
        passagesByStart.values().forEach(v -> v.remove("start"));
        final List<List<String>> result2 =
                continuePath(singletonList("start"), passagesByStart, true)
                        .filter(list -> "end".equals(list.get(list.size() - 1)))
                        .collect(Collectors.toList());
        System.out.println(result2);
        assertThat(result2).hasSize(36);
    }

    @Test
    void pathsToLargerMap() {
        final Map<String, Set<String>> passagesByStart = readExample(LARGE_SAMPLE)
                .map(s -> s.split("-"))
                .flatMap(a -> Stream.of(new CavePassage(a[0], a[1]), new CavePassage(a[1], a[0])))
                .collect(Collectors.groupingBy(cp -> cp.from, Collectors.mapping(cp -> cp.to, Collectors.toSet())));
        passagesByStart.values().forEach(v -> v.remove("start"));
        final List<List<String>> result2 =
                continuePath(singletonList("start"), passagesByStart, false)
                        .filter(list -> "end".equals(list.get(list.size() - 1)))
                        .collect(Collectors.toList());
        System.out.println(result2);
        assertThat(result2).hasSize(19);
    }

    @Test
    void pathsToLargerMapWithRevisit() {
        final Map<String, Set<String>> passagesByStart = readExample(LARGE_SAMPLE)
                .map(s -> s.split("-"))
                .flatMap(a -> Stream.of(new CavePassage(a[0], a[1]), new CavePassage(a[1], a[0])))
                .collect(Collectors.groupingBy(cp -> cp.from, Collectors.mapping(cp -> cp.to, Collectors.toSet())));
        passagesByStart.values().forEach(v -> v.remove("start"));
        final List<List<String>> result2 =
                continuePath(singletonList("start"), passagesByStart, true)
                        .filter(list -> "end".equals(list.get(list.size() - 1)))
                        .collect(Collectors.toList());
        System.out.println(result2);
        assertThat(result2).hasSize(103);
    }

    @Test
    void pathsToPuzzleMap() {
        final Map<String, Set<String>> passagesByStart = readFile()
                .map(s -> s.split("-"))
                .flatMap(a -> Stream.of(new CavePassage(a[0], a[1]), new CavePassage(a[1], a[0])))
                .collect(Collectors.groupingBy(cp -> cp.from, Collectors.mapping(cp -> cp.to, Collectors.toSet())));
        passagesByStart.values().forEach(v -> v.remove("start"));
        final List<List<String>> result2 =
                continuePath(singletonList("start"), passagesByStart, false)
                        .filter(list -> "end".equals(list.get(list.size() - 1)))
                        .collect(Collectors.toList());
        System.out.println(result2);
        assertThat(result2).hasSize(4413);
    }

    @Test
    void pathsToPuzzleMapWithRevisit() {
        final Map<String, Set<String>> passagesByStartPoint = readFile()
                .map(s -> s.split("-"))
                .flatMap(a -> Stream.of(new CavePassage(a[0], a[1]), new CavePassage(a[1], a[0])))
                .collect(Collectors.groupingBy(cp -> cp.from, Collectors.mapping(cp -> cp.to, Collectors.toSet())));
        passagesByStartPoint.values().forEach(v -> v.remove("start"));
        final List<List<String>> result2 =
                continuePath(singletonList("start"), passagesByStartPoint, true)
                        .filter(list -> "end".equals(list.get(list.size() - 1)))
                        .collect(Collectors.toList());
        System.out.println(result2);
        assertThat(result2).hasSize(118803);
    }

    private Set<String> neighbours(final String currentCave, final Map<String, Set<String>> availablePaths) {
        return availablePaths.get(currentCave);
    }

    private Stream<List<String>> continuePath(final List<String> currentPath,
            final Map<String, Set<String>> availablePaths, final boolean revisitingAllowed) {
//        System.out.println("Already traversed " + currentPath);
        final boolean smallCaveAlreadyVisitedTwice =
                currentPath.stream().filter(cave -> cave.equals(cave.toLowerCase()))
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                        .values()
                        .stream()
                        .anyMatch(v -> v > 1);
        final Set<String> visitedSmallCaves = currentPath.stream().filter(cave -> cave.equals(cave.toLowerCase()))
                .collect(Collectors.toSet());
        final var endOfCurrentPath = currentPath.get(currentPath.size() - 1);
        final Set<String> pathCandidates = availablePaths.getOrDefault(endOfCurrentPath, emptySet());
        if (!revisitingAllowed || smallCaveAlreadyVisitedTwice) {
            pathCandidates.removeAll(visitedSmallCaves);
        }
        if (pathCandidates.isEmpty() || "end".equals(endOfCurrentPath)) {
            return Stream.of(currentPath);
        } else {
            return pathCandidates.stream().flatMap(candidate -> continuePath(augmentPath(currentPath, candidate),
                    copyPaths(availablePaths),
                    revisitingAllowed && !smallCaveAlreadyVisitedTwice));
        }

    }

    private Map<String, Set<String>> copyPaths(final Map<String, Set<String>> availablePaths) {
        final HashMap<String, Set<String>> modifiedAvailablePaths = new HashMap<>();
        availablePaths.forEach((k, v) -> modifiedAvailablePaths.put(k, new HashSet<>(v)));
        return modifiedAvailablePaths;
    }

    private List<String> augmentPath(final List<String> currentPath, final String candidate) {
        final ArrayList<String> newPath = new ArrayList<>(currentPath);
        newPath.add(candidate);
        return newPath;
    }

    private class CavePassage {

        private String from;
        private String to;

        private CavePassage(final String from, final String to) {
            this.from = from;
            this.to = to;
        }
    }

}
