package day20;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Day20 {

    private final String DECODER_SAMPLE =
            "..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####"
                    + "..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#..."
                    + ".###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#."
                    + ".....#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#"
                    + ".##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#."
                    + ".###..#####........#..####......#..#";
    private static final String IMAGE_SAMPLE = """
            #..#.
            #....
            ##..#
            ..#..
            ..###""";

    private String decoderString() {
        var inputStream = Day20.class.getResourceAsStream("/day20.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.lines().limit(1).collect(Collectors.joining(""));
    }

    private String[][] readImageData() {
        var inputStream = Day20.class.getResourceAsStream("/day20.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        final Stream<String[]> stream = buf.lines().skip(2).map(line -> line.split(""));
        final List<String[]> collect = stream.collect(Collectors.toList());
        return collect.toArray(new String[collect.size()][]);
    }

    @Test
    void readFileTest() {
        final var inputImage = readImageData();
        assertThat(inputImage[0][0]).isEqualTo("#");
        assertThat(inputImage[inputImage.length - 1][0]).isEqualTo(".");
    }

    @Test
    void decodeSampleImageTwice() {
        final String[][] image = readSampleImage();

        final String[][] outputImage1 = progressImage(DECODER_SAMPLE, image, ".");
        print(outputImage1);

        final String[][] outputImage2 = progressImage(DECODER_SAMPLE, outputImage1, ".");
        print(outputImage2);

        var count = 0;
        for (int y = 0; y < outputImage2.length; y++) {
            for (int x = 0; x < outputImage2.length; x++) {
                if (outputImage2[y][x].equals("#")) {
                    count++;
                }
            }
        }
        assertThat(count).isEqualTo(35);
    }


    private String[][] readSampleImage() {
        return IMAGE_SAMPLE.lines().map(line -> line.split("")).toList().toArray(new String[0][0]);
    }

    @Test
    void decodeImageTwoSteps() {
        final var decoderString = decoderString();
        final var image = readImageData();

        final String[][] outputImage1 = progressImage(decoderString, image, ".");
        print(outputImage1);
        final String[][] outputImage2 = progressImage(decoderString, outputImage1, "#");
        print(outputImage2);
        var count = 0;
        for (int y = 0; y < outputImage2.length; y++) {
            for (int x = 0; x < outputImage2.length; x++) {
                if (outputImage2[y][x].equals("#")) {
                    count++;
                }
            }
        }
        assertThat(count).isEqualTo(5483);
    }

    @Test
    void decodeImageFiftySteps() {
        final var decoderString = decoderString();
        final var image = readImageData();

        String[][] finalResult = image;
        for (int i = 0; i<25; i++) {
            final String[][] tempResult = progressImage(decoderString, finalResult, ".");
            finalResult = progressImage(decoderString, tempResult, "#");
        }
        var count = 0;
        for (int y = 0; y < finalResult.length; y++) {
            for (int x = 0; x < finalResult.length; x++) {
                if (finalResult[y][x].equals("#")) {
                    count++;
                }
            }
        }
        assertThat(count).isEqualTo(18732);
    }

    @Test
    void testNeighborhoodSample() {
        assertThat(neighbourhoodString(0,0, readSampleImage(), ".")).isEqualTo("....#..#.");
    }

    private String[][] progressImage(final String decoderString, final String[][] image, final String defaultValue) {
        final var borderSize = 3;
        final var outputImage = new String[image.length + (2 * borderSize)][image[0].length + (2 * borderSize)];
        for (var newY = 0; newY < outputImage.length; newY++) {
            for (var newX = 0; newX < outputImage[0].length; newX++) {
                outputImage[newY][newX] =
                        successorPixel(newX - borderSize, newY - borderSize, image, defaultValue, decoderString);
            }
        }
        return outputImage;
    }

    private String successorPixel(final int x, final int y, final String[][] image, final String defaultValue,
            final String decoderString) {
        final String neighbourhood = neighbourhoodString(x, y, image, defaultValue);
        final var lookupIndex = Integer.parseInt(neighbourhood.replace(".", "0").replace("#", "1"), 2);
        return Character.toString(decoderString.charAt(lookupIndex));
    }

    private String neighbourhoodString(final int x, final int y, final String[][] image, final String defaultValue) {
        final var lineBuilder = new StringBuilder();
        for (int row = y - 1; row <= y + 1; row++) {
            for (int column = x - 1; column <= x + 1; column++) {
                lineBuilder.append(lookup(column, row, image, defaultValue));
            }
        }
        return lineBuilder.toString();
    }

    private String lookup(final int x, final int y, final String[][] grid, final String defaultValue) {
        if (x < 0 || y < 0 || x >= grid[0].length || y >= grid.length) {
            return defaultValue;
        }
        return grid[y][x];
    }

    private void print(final String[][] image) {
        for (var row : image) {
            for (var cell : row) {
                System.out.print(cell);
            }
            System.out.println();
        }
    }
}
