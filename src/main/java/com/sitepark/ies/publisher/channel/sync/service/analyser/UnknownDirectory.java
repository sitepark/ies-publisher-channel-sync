package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserContext;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import java.io.IOException;

public class UnknownDirectory implements PublishedPathAnalyser {

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) throws IOException {

    if (!path.isDirectory()) {
      return AnalyserResult.OK;
    }

    if (!ctx.isRecursive()) {
      return AnalyserResult.OK;
    }

    PublicationDirectory child = ctx.getIesDirectory().getChild(path.baseName());
    if (child != null) {
      return AnalyserResult.OK;
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    return resultFactory.createResult(ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
  }
}
