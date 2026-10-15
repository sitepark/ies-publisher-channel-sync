package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;
import java.nio.file.Path;

public class MediaMetaFile implements PublishedPathAnalyser {

  private static final String SUFFIX = ".meta.php";

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) {

    if (path.isDirectory()) {
      return AnalyserResult.OK;
    }

    String name = path.baseName();
    if (!name.endsWith(SUFFIX)) {
      return AnalyserResult.OK;
    }

    if (ctx.getChannel().layout() == ChannelLayout.ID_BASED_RESOURCES) {
      AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();

      return resultFactory.createResult(ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
    }

    Path parent = path.absolutePath().getParent();
    if (parent == null) {
      throw new IllegalArgumentException("The parent path must not be null");
    }

    String mediaFileName = name.substring(0, name.lastIndexOf(SUFFIX));

    for (Publication p : ctx.getPublicationDirectory().getPublications(mediaFileName)) {
      if (p.isPublished()) {
        return AnalyserResult.OK_AND_RECURSIVE_INTERRUPT;
      }
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    return resultFactory.createRecursiveInterruptResultDeleteForce(
        ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
  }
}
