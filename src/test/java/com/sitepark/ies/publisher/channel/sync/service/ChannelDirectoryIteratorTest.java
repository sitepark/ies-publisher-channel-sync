package com.sitepark.ies.publisher.channel.sync.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import java.nio.file.Path;
import java.util.Iterator;
import org.junit.jupiter.api.Test;

class ChannelDirectoryIteratorTest {

  @Test
  void testHasNext() {

    Iterator<Path> pathIterator = mock();
    when(pathIterator.hasNext()).thenReturn(true);

    ChannelDirectoryIterator iterator =
        new ChannelDirectoryIterator(PublicationType.OBJECT, pathIterator);

    assertTrue(iterator.hasNext(), "Should have next");
  }

  @Test
  void testNext() {

    Path a = Path.of("a");

    Iterator<Path> pathIterator = mock();
    when(pathIterator.hasNext()).thenReturn(true);
    when(pathIterator.next()).thenReturn(a);

    ChannelDirectoryIterator iterator =
        new ChannelDirectoryIterator(PublicationType.OBJECT, pathIterator);

    PublishedPath expected = new PublishedPath(PublicationType.OBJECT, a.toAbsolutePath(), "a");

    assertEquals(expected, iterator.next(), "Unexpected next");
  }

  @Test
  void testNextWithNullFileName() {

    Path a = Path.of("/");

    Iterator<Path> pathIterator = mock();
    when(pathIterator.hasNext()).thenReturn(true);
    when(pathIterator.next()).thenReturn(a);

    ChannelDirectoryIterator iterator =
        new ChannelDirectoryIterator(PublicationType.OBJECT, pathIterator);

    PublishedPath expected = new PublishedPath(PublicationType.OBJECT, a.toAbsolutePath(), null);

    assertEquals(expected, iterator.next(), "Unexpected next");
  }

  @Test
  void testNextWithBlankFileName() {

    Path a = Path.of(" ");

    Iterator<Path> pathIterator = mock();
    when(pathIterator.hasNext()).thenReturn(true);
    when(pathIterator.next()).thenReturn(a);

    ChannelDirectoryIterator iterator =
        new ChannelDirectoryIterator(PublicationType.OBJECT, pathIterator);

    PublishedPath expected = new PublishedPath(PublicationType.OBJECT, a.toAbsolutePath(), null);

    assertEquals(expected, iterator.next(), "Unexpected next");
  }
}
