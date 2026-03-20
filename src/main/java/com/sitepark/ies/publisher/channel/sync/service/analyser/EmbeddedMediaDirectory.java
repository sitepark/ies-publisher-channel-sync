package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationType;
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

    if (ctx.getChannel().layout() == ChannelLayout.ID_BASED_RESOURCES) {
      return this.analyseIdBased(ctx, path);
    } else {
      return this.analyseUrlBased(ctx, path);
    }
  }

  private AnalyserResult analyseUrlBased(AnalyserContext ctx, PublishedPath path) {
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

  private AnalyserResult analyseIdBased(AnalyserContext ctx, PublishedPath path) {

    Path parent = path.absolutePath().getParent();

    if (parent == null) {
      return AnalyserResult.OK;
    }

    Path fileName = parent.getFileName();
    if (fileName == null) {
      return AnalyserResult.OK;
    }
    String name = fileName.toString();
    if (!name.endsWith(SUFFIX)) {
      return AnalyserResult.OK;
    }

    AnalyserResult interimResult = ifMediaOwnerAObjectAndExists(ctx, path);
    if (interimResult != AnalyserResult.OK) {
      return interimResult;
    }

    return ifMediaOwnerAMediumAndExists(ctx, parent, name, path);
  }

  private AnalyserResult ifMediaOwnerAObjectAndExists(AnalyserContext ctx, PublishedPath path) {

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();

    // expect a format dir.media/1234-5677
    String[] parts = path.baseName().split("-");
    if (parts.length != 2) {
      return resultFactory.createRecursiveInterruptResultDeleteForce(
          ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
    }

    long id;

    try {
      id = Long.parseLong(parts[0]);
      Long.parseLong(parts[1]);
    } catch (NumberFormatException e) {
      return resultFactory.createRecursiveInterruptResultDeleteForce(
          ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
    }

    Path mediaOwnerIdPath = ctx.getChannel().pathFor(id);
    Path mediaOwnerPath =
        ctx.getChannel()
            .resolve(
                PublicationType.OBJECT,
                mediaOwnerIdPath.resolveSibling(mediaOwnerIdPath.getFileName() + ".php"));

    if (Files.exists(mediaOwnerPath)) {
      return AnalyserResult.OK_AND_INTERRUPT;
    }

    return AnalyserResult.OK;
  }

  private AnalyserResult ifMediaOwnerAMediumAndExists(
      AnalyserContext ctx, Path parent, String name, PublishedPath path) {
    String mainMedia = name.substring(0, name.lastIndexOf(SUFFIX));
    Path mainMediaParent = parent.getParent();
    if (mainMediaParent != null) {
      Path mediaOwnerPathMainMedia = mainMediaParent.resolve(mainMedia);
      if (Files.exists(mediaOwnerPathMainMedia)) {
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
