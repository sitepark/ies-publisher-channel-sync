package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import java.nio.file.Path;

public class DocumentRootLayoutFiles implements PublishedPathAnalyser {

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) {

    if (ctx.getChannel().layout() != ChannelLayout.DOCUMENT_ROOT) {
      return AnalyserResult.OK;
    }

    Path fullPath = ctx.getChannel().relativize(PublicationType.OBJECT, path.absolutePath());

    String baseName = fullPath.getName(0).toString();

    if ("WEB-IES".equals(baseName)) {
      return AnalyserResult.OK_AND_RECURSIVE_INTERRUPT;
    }

    if (!path.isDirectory()
        && ("aliases.map".equals(baseName) || "redirects.map".equals(baseName))) {
      return AnalyserResult.OK_AND_RECURSIVE_INTERRUPT;
    }

    return AnalyserResult.OK;
  }
}
