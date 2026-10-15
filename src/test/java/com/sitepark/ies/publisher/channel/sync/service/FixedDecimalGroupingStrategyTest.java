package com.sitepark.ies.publisher.channel.sync.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FixedDecimalGroupingStrategyTest {

  private final FixedDecimalGroupingStrategy strategy = new FixedDecimalGroupingStrategy(2, 3);

  @Test
  void testZeroIsPaddedInEveryGroup() {
    assertEquals(
        Path.of("000/000/000"), this.strategy.toPath(0), "every group should be zero padded");
  }

  @Test
  void testIdIsSplitIntoGroupsFromTheRight() {
    assertEquals(
        Path.of("000/001/123"),
        this.strategy.toPath(1123),
        "the last group should be the file name, the leading groups the directories");
  }

  @Test
  void testFullIdUsesAllGroups() {
    assertEquals(
        Path.of("123/456/789"),
        this.strategy.toPath(123_456_789),
        "a nine digit id should fill all three groups");
  }

  @Test
  void testLargestIdIsAccepted() {
    assertEquals(
        Path.of("999/999/999"),
        this.strategy.toPath(999_999_999),
        "the largest id of the layout should be mapped");
  }

  @Test
  void testIdBeyondLayoutIsRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> this.strategy.toPath(1_000_000_000),
        "an id that does not fit into the layout should be rejected");
  }

  @Test
  void testNegativeIdIsRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> this.strategy.toPath(-1),
        "a negative id should be rejected");
  }

  @Test
  void testOneLevelLayout() {
    FixedDecimalGroupingStrategy oneLevel = new FixedDecimalGroupingStrategy(1, 2);
    assertEquals(
        Path.of("12/34"), oneLevel.toPath(1234), "one directory level should precede the file");
  }

  @Test
  void testNegativeLevelsAreRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new FixedDecimalGroupingStrategy(-1, 3),
        "negative levels should be rejected");
  }

  @Test
  void testNonPositiveDigitsPerLevelAreRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new FixedDecimalGroupingStrategy(2, 0),
        "zero digits per level should be rejected");
  }
}
