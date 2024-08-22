package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.service.Channel;
import com.sitepark.ies.publisher.channel.sync.service.analyser.AnalyserContext;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
class AnalyserContextTest {

  @Test
  void testGetResultEntryFactory() {
    AnalyserContext ctx = AnalyserContext.builder().build();
    assertNotNull(ctx.getResultEntryFactory(), "ResultEntryFactory is null");
  }

  @Test
  void testGetAnalyserResultFactory() {
    AnalyserContext ctx = AnalyserContext.builder().build();
    assertNotNull(ctx.getAnalyserResultFactory(), "AnalyserResultFactory is null");
  }

  @Test
  void testGetChannel() {
    Channel channel = mock();
    AnalyserContext ctx = AnalyserContext.builder().channel(channel).build();
    assertEquals(channel, ctx.getChannel(), "Channel is not the same");
  }

  @Test
  void testGetPublicationType() {
    PublicationType publicationType = mock();
    AnalyserContext ctx = AnalyserContext.builder().publicationType(publicationType).build();
    assertEquals(publicationType, ctx.getPublicationType(), "PublicationType is not the same");
  }

  @Test
  void testGetBase() {
    Path base = mock();
    AnalyserContext ctx = AnalyserContext.builder().base(base).build();
    assertEquals(base, ctx.getBase(), "Base is not the same");
  }

  @Test
  void testGetDirectory() {
    Path directory = mock();
    AnalyserContext ctx = AnalyserContext.builder().directory(directory).build();
    assertEquals(directory, ctx.getDirectory(), "Directory is not the same");
  }

  @Test
  void testGetPublicationDirectory() {
    PublicationDirectory directory = mock();
    AnalyserContext ctx = AnalyserContext.builder().publicationDirectory(directory).build();
    assertEquals(directory, ctx.getPublicationDirectory(), "directory is not the same");
  }

  @Test
  void testGetDirectoryEntries() {
    PublishedPath directoryEntry = mock();
    when(directoryEntry.baseName()).thenReturn("basename");
    AnalyserContext ctx = AnalyserContext.builder().directoryEntry(directoryEntry).build();
    assertThat(
        "DirectoryEntries is not the same",
        Arrays.asList(directoryEntry),
        containsInAnyOrder(ctx.getDirectoryEntries().toArray()));
  }

  @Test
  void testHasDirectoryEntry() {
    PublishedPath directoryEntry = mock();
    when(directoryEntry.baseName()).thenReturn("basename");
    AnalyserContext ctx = AnalyserContext.builder().directoryEntry(directoryEntry).build();
    assertTrue(
        ctx.hasDirectoryEntry("basename"), "Should have directoryEntry with name 'basename'");
  }

  @Test
  void testDuplicateDirectoryEntries() {

    PublishedPath a = mock();
    when(a.baseName()).thenReturn("basename");
    PublishedPath b = mock();
    when(b.baseName()).thenReturn("basename");

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          AnalyserContext.builder().directoryEntry(a).directoryEntry(b);
        });
  }

  @Test
  void testIsRecursive() {
    AnalyserContext ctx = AnalyserContext.builder().recursive(true).build();
    assertEquals(true, ctx.isRecursive(), "Recursive is not the same");
  }
}
