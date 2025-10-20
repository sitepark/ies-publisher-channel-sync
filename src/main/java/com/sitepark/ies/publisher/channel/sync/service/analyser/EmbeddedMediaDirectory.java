package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;
import java.nio.file.Files;
import java.nio.file.Path;

public class EmbeddedMediaDirectory implements PublishedPathAnalyser, PublicationAnalyser {

  private static final String SUFFIX = ".media";

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) {

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

    String mediaOwnerName = name.substring(0, name.lastIndexOf(SUFFIX));

    for (Publication p : ctx.getPublicationDirectory().getPublications(mediaOwnerName)) {
      if (p.isPublished()) {
        return AnalyserResult.OK_AND_INTERRUPT;
      }
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    return resultFactory.createRecursiveInterruptResultDeleteForce(
        ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
  }

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, Publication publication) {

    Path path = publication.absolutePath();
    Path mediaId = path.getParent();
    if (mediaId == null) {
      return AnalyserResult.OK;
    }
    Path mediaDir = mediaId.getParent();
    if (mediaDir == null) {
      return AnalyserResult.OK;
    }

    if (!mediaDir.toString().endsWith(SUFFIX)) {
      return AnalyserResult.OK;
    }

    if (Files.exists(path)) {
      return AnalyserResult.OK_AND_RECURSIVE_INTERRUPT;
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    return resultFactory.createRecursiveInterruptResult(ResultType.MISSING_FILE, publication);
  }
}
