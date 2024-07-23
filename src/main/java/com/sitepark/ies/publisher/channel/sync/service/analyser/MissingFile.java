package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserContext;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MissingFile implements PublicationAnalyser {

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, Publication publication) throws IOException {

    if (ctx.getPublicationType() != publication.type()) {
      return AnalyserResult.OK;
    }

    if (ctx.hasDirectoryEntry(publication.fileName())) {
      return AnalyserResult.OK;
    }

    if (publication.isCollision()) {
      return AnalyserResult.OK;
    }

    if (!publication.isPublished()) {
      return AnalyserResult.OK;
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();

    if (publication.path().toString().isEmpty()) {
      return resultFactory.createInterruptResult(ResultType.LOST_PUBLICATION, publication);
    }

    Path path = ctx.getChannel().resolve(publication.type(), publication.path());
    if (Files.isRegularFile(path)) {
      return AnalyserResult.OK;
    }

    return resultFactory.createInterruptResult(ResultType.MISSING_FILE, publication);
  }
}
