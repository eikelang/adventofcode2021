package day16;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class Day16 {

    private static final String SAMPLE_PACKET = "D2FE28";

    private String readFile() throws IOException {
        var inputStream = Day16.class.getResourceAsStream("/day16.txt");
        var fis = new InputStreamReader(inputStream);
        var buf = new BufferedReader(fis);
        return buf.readLine();
    }

    @Test
    void getVersionAndTypeInfoFromSample() {
        final Packet packet = new Packet(new StatefulParsing(hexToBinary(SAMPLE_PACKET)));
        assertThat(packet.version()).isEqualTo(6);
    }

    @Test
    void getValueFromLiteral() {
        final Packet packet = new Packet(new StatefulParsing(hexToBinary(SAMPLE_PACKET)));
        assertThat(packet.value()).isEqualTo(2021);
    }

    @Test
    void getVersionFromOperatorPacket() {
        final Packet packet = new Packet(new StatefulParsing(hexToBinary("38006F45291200")));
        assertThat(packet.version()).isEqualTo(1);
    }

    @Test
    void getSubpacketsFromOperatorPacket() {
        final Packet packet = new Packet(new StatefulParsing(hexToBinary("38006F45291200")));
        assertThat(packet.subPackets()).extracting(Packet::value)
                .containsExactly(10L, 20L);
    }

    @Test
    void versionSum1() {
        final Packet packet = new Packet(new StatefulParsing(hexToBinary("8A004A801A8002F478")));
        final var versionSum = packet.versionSum();
        assertThat(versionSum).isEqualTo(16);
    }

    @Test
    void versionSum2() {
        final Packet packet = new Packet(new StatefulParsing(hexToBinary("620080001611562C8802118E34")));
        final var versionSum = packet.versionSum();
        assertThat(versionSum).isEqualTo(12);
    }

    @Test
    void versionSum3() {
        final Packet packet = new Packet(new StatefulParsing(hexToBinary("C0015000016115A2E0802F182340")));
        final var versionSum = packet.versionSum();
        assertThat(versionSum).isEqualTo(23);
    }

    @Test
    void versionSum4() {
        final Packet packet = new Packet(new StatefulParsing(hexToBinary("A0016C880162017C3686B18A3D4780")));
        final var versionSum = packet.versionSum();
        assertThat(versionSum).isEqualTo(31);
    }

    @Test
    void versionSumFile() throws IOException {
        final Packet packet = new Packet(new StatefulParsing(hexToBinary(readFile())));
        final var versionSum = packet.versionSum();
        assertThat(versionSum).isEqualTo(821L);
    }

    @Test
    void valueFile() throws IOException {
        final Packet packet = new Packet(new StatefulParsing(hexToBinary(readFile())));
        System.out.println(packet);
        assertThat(packet.value()).isEqualTo(2056021084691L);
    }

    @Test
    void testExceptionForUnsupportedOperationJustToGet100Percent() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                SubpacketsOperation.fromTypeId(23));
    }

    private static List<String> hexToBinary(final String input) {
        return input.chars().boxed()
                .map(c -> Integer.parseInt(Character.toString(c), 16))
                .map(c -> Integer.toString(c, 2))
                .map(Day16::padToFour)
                .flatMap(s -> Arrays.stream(s.split("")))
                .toList();
    }

    private static String padToFour(final String input) {
        final var toPad = 4 - input.length();
        return "0".repeat(toPad) + input;
    }

}
