package com.sitepark.ies.publisher.channel.sync.domain.entity;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class PublicationTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(Publication.class).verify();
  }
}
