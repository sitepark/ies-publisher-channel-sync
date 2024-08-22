package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import com.sitepark.ies.publisher.channel.sync.service.Channel;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class CollisionTest extends AnalyserTest {

  private final Path resourceDir = Path.of("src/test/resources/service/analyser/CollisionTest");

  private final Collision analyser = new Collision();

  @Test
  void testWhenPublishedPathIsNotADirectory() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();
    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(false);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithoutCollision() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    PublicationDirectory directory = mock();
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);

    assertEquals(AnalyserResult.OK, this.analyser.analyse(ctx, path), "Should return OK");
  }

  @Test
  void testWithExistsCollision() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    Path collisionFile = this.resourceDir.resolve("collisionfile");

    Channel channel = mock();
    when(channel.resolve(any(PublicationType.class), any(Path.class))).thenReturn(collisionFile);
    when(ctx.getChannel()).thenReturn(channel);

    PublicationDirectory directory = mock();

    Publication publication = mock();
    when(directory.getCollision(any())).thenReturn(publication);
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);

    Publication collision = mock();
    when(directory.getCollision(any())).thenReturn(collision);
    when(collision.type()).thenReturn(mock());
    when(collision.path()).thenReturn(mock());

    AnalyserResult expected =
        ctx.getAnalyserResultFactory()
            .createInterruptResult(ResultType.LEGAL_FILENAME_COLLISION, collision);

    assertEquals(expected, this.analyser.analyse(ctx, path), "Unexpected result");
  }

  @Test
  void testWithNonExistsCollision() throws IOException {
    AnalyserContext ctx = this.mockAnalyserContext();

    Path collisionFile = this.resourceDir.resolve("non-exists-collisionfile");

    Channel channel = mock();
    when(channel.resolve(any(PublicationType.class), any(Path.class))).thenReturn(collisionFile);
    when(ctx.getChannel()).thenReturn(channel);

    PublicationDirectory directory = mock();

    Publication publication = mock();
    when(directory.getCollision(any())).thenReturn(publication);
    when(ctx.getPublicationDirectory()).thenReturn(directory);

    PublishedPath path = mock();
    when(path.isDirectory()).thenReturn(true);

    Publication collision = mock();
    when(directory.getCollision(any())).thenReturn(collision);
    when(collision.type()).thenReturn(mock());
    when(collision.path()).thenReturn(mock());

    AnalyserResult expected =
        ctx.getAnalyserResultFactory().createInterruptResult(ResultType.MISSING_FILE, collision);

    assertEquals(expected, this.analyser.analyse(ctx, path), "Unexpected result");
  }
}
