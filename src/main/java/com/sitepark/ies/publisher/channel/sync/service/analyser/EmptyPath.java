package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserContext;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntryFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmptyPath implements PublishedPathAnalyser {

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) throws IOException {

    if (path.isDirectory()) {
      return AnalyserResult.OK;
    }

    List<ResultEntry> list = new ArrayList<ResultEntry>();

    ResultEntryFactory resultEntryFactory = ctx.getResultEntryFactory();

    for (Publication p : ctx.getPublicationDirectory().getPublications(path.baseName())) {

      if (!p.isPublished()) {
        continue;
      }

      if (!p.path().toString().isEmpty()) {
        continue;
      }

      /*
       *  check: P - lost publication, C - filename collision
       *
       *  If several publications exist for this file name, but none have a path,
       *  it is not possible to determine which one is now published. The first
       *  one is then used. If there are others, these are illegal collisions.
       */
      ResultType type =
          list.isEmpty() ? ResultType.LOST_PUBLICATION : ResultType.ILLEGAL_FILENAME_COLLISION;

      list.add(resultEntryFactory.createResultEntry(type, p));
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    return resultFactory.createResult(list);
  }
}
