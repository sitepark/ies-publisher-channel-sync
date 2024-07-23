package com.sitepark.ies.publisher.channel.sync.domain.entity;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class PublicationDirectoryTest {

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    PublicationDirectory b = PublicationDirectory.builder().name("b").build();
    PublicationDirectory a = PublicationDirectory.builder().name("a").child(b).build();
    EqualsVerifier.forClass(PublicationDirectory.class)
        .withPrefabValues(PublicationDirectory.class, a, b)
        .withIgnoredFields("parent")
        .verify();
  }
}
