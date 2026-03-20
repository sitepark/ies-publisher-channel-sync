package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;

public class EmailConfigDirectory implements PublishedPathAnalyser {

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) {

    if (ctx.getPublicationType() != PublicationType.CONFIG || !path.isDirectory()) {
      return AnalyserResult.OK;
    }

    if (!path.baseName().equals("email")) {
      return AnalyserResult.OK;
    }

    return AnalyserResult.OK_AND_RECURSIVE_INTERRUPT;
  }
}
