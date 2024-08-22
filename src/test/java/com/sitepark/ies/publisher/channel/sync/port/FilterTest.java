package com.sitepark.ies.publisher.channel.sync.port;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import org.junit.jupiter.api.Test;

class FilterTest {

  @Test
  void testAcceptAll() {
    PublishedPath name = mock();
    assertTrue(Filter.ACCEPT_ALL.accept(name), "Should accept all");
  }
}
