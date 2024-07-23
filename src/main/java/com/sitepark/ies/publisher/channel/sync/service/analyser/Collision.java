package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserContext;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Collision implements PublishedPathAnalyser {

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) throws IOException {

    if (!path.isDirectory()) {
      return AnalyserResult.OK;
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();

    Publication collision = ctx.getIesDirectory().getCollision(path.baseName());
    if (collision == null) {
      return AnalyserResult.OK;
    }

    Path collisionFile = ctx.getChannel().resolve(collision.type(), collision.path());
    if (Files.exists(collisionFile)) {
      return resultFactory.createInterruptResult(ResultType.LEGAL_FILENAME_COLLISION, collision);
    } else {
      return resultFactory.createInterruptResult(ResultType.MISSING_FILE, collision);
    }
  }
}
