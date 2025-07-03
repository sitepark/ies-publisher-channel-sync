package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;

public class DocumentRootLayoutFiles implements PublishedPathAnalyser {

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) {

    if (ctx.getChannel().layout() != ChannelLayout.DOCUMENT_ROOT) {
      return AnalyserResult.OK;
    }

    String baseName = path.baseName();

    if (path.isDirectory() && "WEB-IES".equals(baseName)) {
      return AnalyserResult.OK_AND_INTERRUPT;
    }

    if (!path.isDirectory()
        && ("aliases.map".equals(baseName) || "redirects.map".equals(baseName))) {
      return AnalyserResult.OK_AND_INTERRUPT;
    }

    return AnalyserResult.OK;
  }
}
