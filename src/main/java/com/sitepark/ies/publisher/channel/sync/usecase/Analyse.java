package com.sitepark.ies.publisher.channel.sync.usecase;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserContext;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationType;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ResultEntry;
import com.sitepark.ies.publisher.channel.sync.port.Filter;
import com.sitepark.ies.publisher.channel.sync.port.Hasher;
import com.sitepark.ies.publisher.channel.sync.port.Publisher;
import com.sitepark.ies.publisher.channel.sync.service.Channel;
import com.sitepark.ies.publisher.channel.sync.service.PublicationDirectoryTreeBuilder;
import com.sitepark.ies.publisher.channel.sync.service.analyser.Collision;
import com.sitepark.ies.publisher.channel.sync.service.analyser.DirectoryFileMismatch;
import com.sitepark.ies.publisher.channel.sync.service.analyser.EmbeddedMediaDirectory;
import com.sitepark.ies.publisher.channel.sync.service.analyser.EmptyPath;
import com.sitepark.ies.publisher.channel.sync.service.analyser.FileDirectoryMismatch;
import com.sitepark.ies.publisher.channel.sync.service.analyser.HashMismatch;
import com.sitepark.ies.publisher.channel.sync.service.analyser.MediaMetaFile;
import com.sitepark.ies.publisher.channel.sync.service.analyser.MissingFile;
import com.sitepark.ies.publisher.channel.sync.service.analyser.PublicationAnalyser;
import com.sitepark.ies.publisher.channel.sync.service.analyser.PublicationTypeMismatch;
import com.sitepark.ies.publisher.channel.sync.service.analyser.PublishedPathAnalyser;
import com.sitepark.ies.publisher.channel.sync.service.analyser.ScaledImageDirectory;
import com.sitepark.ies.publisher.channel.sync.service.analyser.TranslationsDirectory;
import com.sitepark.ies.publisher.channel.sync.service.analyser.UnknownDirectory;
import com.sitepark.ies.publisher.channel.sync.service.analyser.UnknownFile;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects"})
public class Analyse {

  private final Channel channel;

  private final Publisher publisher;

  private final Filter filter;

  private final List<PublishedPathAnalyser> publishedPathAnalysers;

  private final List<PublicationAnalyser> publicationAnalysers;

  public Analyse(Channel channel, Publisher publisher, Hasher hasher) {
    this(channel, publisher, Filter.ACCEPT_ALL, hasher);
  }

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public Analyse(Channel channel, Publisher publisher, Filter filter, Hasher hasher) {
    this.channel = channel;
    this.publisher = publisher;
    this.filter = filter;

    this.publishedPathAnalysers =
        Arrays.asList(
            new FileDirectoryMismatch(),
            new DirectoryFileMismatch(),
            new PublicationTypeMismatch(),
            new ScaledImageDirectory(),
            new TranslationsDirectory(),
            new EmbeddedMediaDirectory(),
            new MediaMetaFile(),
            new Collision(),
            new UnknownDirectory(),
            new HashMismatch(hasher),
            new UnknownFile(),
            new EmptyPath());
    this.publicationAnalysers = Arrays.asList(new EmbeddedMediaDirectory(), new MissingFile());
  }

  public AnalyserResult analyse(Path base, boolean recursive) throws IOException {
    PublicationDirectory iesDirectory = this.buildPublicationDirectory(base);

    List<PublicationType> types = new ArrayList<>();
    if (this.channel.getLayout() == ChannelLayout.DOCUMENT_ROOT) {
      types.add(PublicationType.OBJECT);
    } else {
      types.addAll(Arrays.asList(PublicationType.values()));
    }

    List<ResultEntry> list = new ArrayList<ResultEntry>();
    for (PublicationType type : types) {
      AnalyserResult result = this.analyseByType(iesDirectory, type, base, recursive);
      list.addAll(result.entries());
    }

    for (PublicationType type : types) {
      AnalyserContext ctx =
          this.createAnalyserContext(type, base, Path.of(""), iesDirectory, recursive);
      AnalyserResult publicationsResult = this.analysePublications(ctx);
      list.addAll(publicationsResult.entries());
    }

    return new AnalyserResult(list, false);
  }

  public AnalyserResult analyseByType(
      PublicationDirectory iesDirectory, PublicationType type, Path base, boolean recursive)
      throws IOException {
    AnalyserContext ctx =
        this.createAnalyserContext(type, base, Path.of(""), iesDirectory, recursive);
    return this.analyse(ctx);
  }

  private PublicationDirectory buildPublicationDirectory(Path base) {
    if (base.isAbsolute()) {
      throw new IllegalArgumentException("The path must be relative to publication channel base");
    }
    PublicationDirectoryTreeBuilder builder = new PublicationDirectoryTreeBuilder();
    for (Publication publication : this.publisher.getPublications(base)) {
      builder.add(publication);
    }
    PublicationDirectory directory = builder.build();
    Path name = base.getFileName();
    if (name == null || base.toString().isEmpty()) {
      return directory;
    }

    directory = directory.findChild(base);
    if (directory == null) {
      throw new IllegalArgumentException("No publications for directory " + base);
    }

    return directory;
  }

  private AnalyserContext createAnalyserContext(
      PublicationType type,
      Path base,
      Path directory,
      PublicationDirectory iesDirectory,
      boolean recursive)
      throws IOException {

    AnalyserContext.Builder ctxBuilder =
        AnalyserContext.builder()
            .channel(this.channel)
            .publicationType(type)
            .base(base)
            .directory(directory)
            .iesDirectory(iesDirectory)
            .recursive(recursive);

    Path contextDir = base.resolve(directory);

    Path realPath = this.channel.resolve(type, contextDir);

    if (!Files.isDirectory(realPath)) {
      return ctxBuilder.build();
    }

    try (DirectoryStream<PublishedPath> stream =
        this.channel.newDirectoryStream(type, contextDir)) {
      for (PublishedPath path : stream) {
        ctxBuilder.directoryEntry(path);
      }
    }

    return ctxBuilder.build();
  }

  private AnalyserResult analyse(AnalyserContext ctx) throws IOException {

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();

    AnalyserResult directoriesResult = this.analyseDirectories(ctx);
    if (directoriesResult.interrupt()) {
      return directoriesResult;
    }

    List<ResultEntry> list = new ArrayList<ResultEntry>();
    list.addAll(directoriesResult.entries());

    /*
    AnalyserResult publicationsResult = this.analysePublications(ctx);
    list.addAll(publicationsResult.entries());
    */

    return resultFactory.createResult(list);
  }

  private AnalyserResult analyseDirectories(AnalyserContext ctx) throws IOException {

    List<ResultEntry> list = new ArrayList<ResultEntry>();

    for (PublishedPath path : ctx.getDirectoryEntries()) {

      AnalyserResult result = this.analysePublishedPath(ctx, path);
      list.addAll(result.entries());
      if (result.interrupt()) {
        break;
      }
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    return resultFactory.createResult(list);
  }

  private AnalyserResult analysePublishedPath(AnalyserContext ctx, PublishedPath path)
      throws IOException {

    if (!this.filter.accept(path)) {
      return AnalyserResult.OK;
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();

    List<ResultEntry> list = new ArrayList<ResultEntry>();

    for (PublishedPathAnalyser publishedPathAnalyser : this.publishedPathAnalysers) {
      AnalyserResult result = publishedPathAnalyser.analyse(ctx, path);
      list.addAll(result.entries());
      if (result.interrupt()) {
        return resultFactory.createResult(list);
      }
    }

    if (!ctx.isRecursive()) {
      return resultFactory.createResult(list);
    }

    PublicationDirectory child = ctx.getIesDirectory().getChild(path.baseName());
    if (child != null) {
      AnalyserContext childCtx =
          this.createAnalyserContext(
              ctx.getPublicationType(),
              ctx.getBase(),
              ctx.getDirectory().resolve(path.baseName()),
              child,
              ctx.isRecursive());
      AnalyserResult childResult = this.analyse(childCtx);
      list.addAll(childResult.entries());
    }

    return resultFactory.createResult(list);
  }

  private AnalyserResult analysePublications(AnalyserContext ctx) throws IOException {

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    List<ResultEntry> list = new ArrayList<ResultEntry>();

    for (Publication publication : ctx.getIesDirectory().getPublications(false)) {

      if (ctx.getChannel().getLayout() == ChannelLayout.RESOURCES
          && ctx.getPublicationType() != publication.type()) {
        continue;
      }

      AnalyserResult result = this.analysePublication(ctx, publication);
      list.addAll(result.entries());
      if (result.interrupt()) {
        break;
      }
    }

    for (PublicationDirectory child : ctx.getIesDirectory().getChildren()) {
      AnalyserContext childCtx =
          this.createAnalyserContext(
              ctx.getPublicationType(),
              ctx.getBase(),
              ctx.getDirectory().resolve(child.getName()),
              child,
              ctx.isRecursive());
      AnalyserResult childResult = this.analysePublications(childCtx);
      list.addAll(childResult.entries());
    }

    return resultFactory.createResult(list);
  }

  private AnalyserResult analysePublication(AnalyserContext ctx, Publication publication)
      throws IOException {

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    List<ResultEntry> list = new ArrayList<ResultEntry>();
    for (PublicationAnalyser pathAnalyser : this.publicationAnalysers) {
      AnalyserResult result = pathAnalyser.analyse(ctx, publication);
      list.addAll(result.entries());
      if (result.interrupt()) {
        break;
      }
    }

    return resultFactory.createResult(list);
  }
}
