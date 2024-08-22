package com.sitepark.ies.publisher.channel.sync.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PublicationChannelDirectoryStreamTest {

  private final Path root =
      Path.of("src/test/resources/service/PublicationChannelDirectoryStreamTest");

  @Test
  void test() throws IOException {

    Path directory = this.root.resolve("test");
    try (ChannelDirectoryStream stream =
        new ChannelDirectoryStream(PublicationType.OBJECT, Files.newDirectoryStream(directory))) {

      List<PublishedPath> list = new ArrayList<>();
      for (PublishedPath path : stream) {
        list.add(path);
      }

      Path a = Path.of("a");
      Path b = Path.of("b");

      PublishedPath[] expected =
          new PublishedPath[] {
            new PublishedPath(PublicationType.OBJECT, directory.resolve(a).toAbsolutePath(), "a"),
            new PublishedPath(PublicationType.OBJECT, directory.resolve(b).toAbsolutePath(), "b")
          };

      assertThat("The list of paths should be equal", list, containsInAnyOrder(expected));
    }
  }
}
