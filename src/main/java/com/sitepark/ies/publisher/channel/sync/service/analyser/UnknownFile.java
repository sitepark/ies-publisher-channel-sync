package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;

public class UnknownFile implements PublishedPathAnalyser {

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) {

    if (path.isDirectory()) {
      return AnalyserResult.OK;
    }

    for (Publication p : ctx.getPublicationDirectory().getPublications(path.baseName())) {
      if (p.isPublished()) {
        return AnalyserResult.OK;
      }
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    return resultFactory.createResult(ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
  }
}
