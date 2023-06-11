package pl.edu.pw.ee.pz.sharedkernel.function;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class MapUtilTest {

  @Test
  void should_map_entry() {
    // given
    var map = Map.of("First", 1, "Second", 2, "Third", 3);

    // when
    var transformed = map.entrySet().stream()
        .map(MapUtil.mapEntry("(%s: %d)"::formatted))
        .collect(Collectors.joining(";"));

    // then
    assertThat(transformed)
        .contains("(First: 1)")
        .contains("(Second: 2)")
        .contains("(Third: 3)");
    assertThat(transformed.split(";"))
        .contains("(First: 1)")
        .contains("(Second: 2)")
        .contains("(Third: 3)");
  }
}