package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;
import com.sitepark.ies.publisher.channel.sync.service.Channel;
import java.nio.file.Files;
import java.nio.file.Path;

public class Collision implements PublishedPathAnalyser {

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) {

    if (!path.isDirectory()) {
      return AnalyserResult.OK;
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();

    PublicationDirectory directory = ctx.getPublicationDirectory();
    Publication collision = directory.getCollision(path.baseName());
    if (collision == null) {
      return AnalyserResult.OK;
    }

    Channel channel = ctx.getChannel();
    Path collisionFile = channel.resolve(collision.type(), collision.path());
    if (Files.exists(collisionFile)) {
      return resultFactory.createRecursiveInterruptResult(
          ResultType.LEGAL_FILENAME_COLLISION, collision);
    } else {
      return resultFactory.createRecursiveInterruptResult(ResultType.MISSING_FILE, collision);
    }
  }
}
