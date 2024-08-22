package com.sitepark.ies.publisher.channel.sync.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RefTest {

  @Test
  void testId() {
    assertEquals("abc", new Ref("abc").id(), "unexpected id");
  }
}
