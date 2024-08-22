package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntryFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PublicationTypeMismatch implements PublishedPathAnalyser {

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) throws IOException {

    if (path.isDirectory()) {
      return AnalyserResult.OK;
    }

    if (ctx.getChannel().getLayout() != ChannelLayout.RESOURCES) {
      return AnalyserResult.OK;
    }

    ResultEntryFactory resultEntryFactory = ctx.getResultEntryFactory();

    List<ResultEntry> list = new ArrayList<ResultEntry>();

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
