package com.sitepark.ies.publisher.channel.sync.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PublicationDirectoryTreeBuilderTest {

  @Test
  @SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
  void testBuild() {

    PublicationDirectoryTreeBuilder builder = new PublicationDirectoryTreeBuilder();

    Publication a = this.createPublication("a");
    Publication b = this.createPublication("b");
    Publication d = this.createPublication("c/d");
    Publication e = this.createPublication("c/e");
    Publication g = this.createPublication("c/f/g");

    builder.add(a);
    builder.add(b);
    builder.add(d);
    builder.add(e);
    builder.add(g);

    PublicationDirectory expected =
        PublicationDirectory.builder()
            .publication(a)
            .publication(b)
            .child(
                PublicationDirectory.builder()
                    .name("c")
                    .publication(d)
                    .publication(e)
                    .child(PublicationDirectory.builder().name("f").publication(g).build())
                    .build())
            .build();

    PublicationDirectory directory = builder.build();

    assertEquals(expected, directory, "The directory tree should be equal");
  }

  @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
  private Publication createPublication(String path) {

    Path p = Path.of(path);

    return Publication.builder().type(PublicationType.OBJECT).path(p).build();
  }
}
