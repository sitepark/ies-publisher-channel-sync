package com.sitepark.ies.publisher.channel.sync.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ChannelDirectoryStreamTest {

  @Test
  void testIterator() throws IOException {
    DirectoryStream<Path> stream = mock();
    try (ChannelDirectoryStream channelDirectoryStream =
        new ChannelDirectoryStream(PublicationType.OBJECT, stream)) {
      assertNotNull(channelDirectoryStream.iterator(), "Should return an iterator");
    }
  }

  @Test
  void testSecondIteratorCall() throws IOException {
    DirectoryStream<Path> stream = mock();
    try (ChannelDirectoryStream channelDirectoryStream =
        new ChannelDirectoryStream(PublicationType.OBJECT, stream)) {
      channelDirectoryStream.iterator();
      assertThrows(IllegalStateException.class, () -> channelDirectoryStream.iterator());
    }
  }

  @Test
  void testClose() throws IOException {
    DirectoryStream<Path> stream = mock();
    ChannelDirectoryStream channelDirectoryStream =
        new ChannelDirectoryStream(PublicationType.OBJECT, stream);
    channelDirectoryStream.close();

    verify(stream, times(1)).close();
  }
}
