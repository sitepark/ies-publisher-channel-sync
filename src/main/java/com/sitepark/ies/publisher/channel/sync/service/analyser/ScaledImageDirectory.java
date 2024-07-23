package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserContext;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultType;
import java.io.IOException;
import java.nio.file.Path;

public class ScaledImageDirectory implements PublishedPathAnalyser {

  private static final String SUFFIX = ".scaled";

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) throws IOException {

    if (!path.isDirectory()) {
      return AnalyserResult.OK;
    }

    String name = path.baseName();
    if (!name.endsWith(SUFFIX)) {
      return AnalyserResult.OK;
    }

    Path parent = path.absolutePath().getParent();
    if (parent == null) {
      throw new IllegalArgumentException("The parent path must not be null");
    }

    String imageFileName = name.substring(0, name.lastIndexOf(SUFFIX));

    for (Publication p : ctx.getIesDirectory().getPublications(imageFileName)) {
      if (p.isPublished()) {
        return AnalyserResult.OK_AND_INTERRUPT;
      }
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    return resultFactory.createInterruptResultDeleteForce(
        ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
  }
}
