package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntryFactory;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;
import java.util.ArrayList;
import java.util.List;

public class PublicationTypeMismatch implements PublishedPathAnalyser {

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) {

    if (path.isDirectory()) {
      return AnalyserResult.OK;
    }

    if (ctx.getChannel().layout() == ChannelLayout.DOCUMENT_ROOT) {
      return AnalyserResult.OK;
    }

    ResultEntryFactory resultEntryFactory = ctx.getResultEntryFactory();

    List<ResultEntry> list = new ArrayList<>();

    for (Publication p : ctx.getPublicationDirectory().getPublications(path.baseName())) {

      if (!p.isPublished()) {
        continue;
      }

      if (path.type() != p.type()) {
        list.add(resultEntryFactory.createResultEntry(ResultType.PUBLICATION_TYPE_MISMATCH, p));
      }
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    return resultFactory.createResult(list);
  }
}
