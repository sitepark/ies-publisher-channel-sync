package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserContext;
import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import java.io.IOException;

public interface PublicationAnalyser {
  public AnalyserResult analyse(AnalyserContext ctx, Publication publication) throws IOException;
}
