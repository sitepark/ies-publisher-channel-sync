package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.entity.ChannelLayout;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublishedPath;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultType;

public class TranslationsDirectory implements PublishedPathAnalyser {

  private static final String URL_BASED_SUFFIX = ".php.translations";
  private static final String ID_BASED_SUFFIX = ".translations";

  @Override
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) {

    if (!path.isDirectory()) {
      return AnalyserResult.OK;
    }

    String name = path.baseName();
    if (!name.endsWith(URL_BASED_SUFFIX) && !name.endsWith(ID_BASED_SUFFIX)) {
      return AnalyserResult.OK;
    }

    String translationBaseName = this.toTranslationBaseName(name, ctx.getChannel().layout());

    for (Publication p : ctx.getPublicationDirectory().getPublications(translationBaseName)) {
      if (p.isPublished()) {
        return AnalyserResult.OK_AND_RECURSIVE_INTERRUPT;
      }
    }

    AnalyserResultFactory resultFactory = ctx.getAnalyserResultFactory();
    return resultFactory.createRecursiveInterruptResultDeleteForce(
        ResultType.UNKNOWN_FILE_OR_DIRECTORY, path);
  }

  public String toTranslationBaseName(String name, ChannelLayout layout) {
    if (layout == ChannelLayout.ID_BASED_RESOURCES) {
      return name.substring(0, name.lastIndexOf(ID_BASED_SUFFIX)) + ".php";
    } else {
      return name.substring(0, name.lastIndexOf(URL_BASED_SUFFIX)) + ".php";
    }
  }
}
