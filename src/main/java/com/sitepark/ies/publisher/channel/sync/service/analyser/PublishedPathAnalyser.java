package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;
import java.io.IOException;

public interface PublishedPathAnalyser {
  public AnalyserResult analyse(AnalyserContext ctx, PublishedPath path) throws IOException;
}
