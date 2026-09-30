package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface PublicationAnalyser {
  AnalyserResult analyse(AnalyserContext ctx, Publication publication);
}
