package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntryFactory;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;
import com.sitepark.ies.publisher.channel.sync.port.Hasher;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HashMismatch implements PublishedPathAnalyser {

  private final Hasher hasher;

  public HashMismatch(Hasher hasher) {
    this.hasher = hasher;
  }

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) {

    if (path.isDirectory()) {
      return AnalyserResult.OK;
    }

    ResultEntryFactory resultEntryFactory = ctx.getResultEntryFactory();

    List<ResultEntry> list = new ArrayList<>();

    for (Publication p : ctx.getPublicationDirectory().getPublications(path.baseName())) {

      if (!p.isPublished()) {
        continue;
      }

      String publicationPath = p.path().toString();

      if (publicationPath.isEmpty()) {
        continue;
      }

      Path pf = p.absolutePath();
      if (!Files.exists(pf)) {
        continue;
      }

      String hash = this.hasher.hash(pf);
      if (!hash.equals(p.hash())) {
        list.add(resultEntryFactory.createResultEntry(ResultType.HASH_MISMATCH, p));
      }
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    return resultFactory.createResult(list);
  }
}
