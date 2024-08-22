package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;

public class FileDirectoryMismatch implements PublishedPathAnalyser {

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) {

    if (path.isDirectory()) {
      return AnalyserResult.OK;
    }

    PublicationDirectory child = ctx.getPublicationDirectory().getChild(path.baseName());
    if (child == null) {
      return AnalyserResult.OK;
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    return resultFactory.createInterruptResult(ResultType.FILE_DIRECTORY_MISMATCH, path);
  }
}
