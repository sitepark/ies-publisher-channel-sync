package com.sitepark.ies.publisher.channel.sync.service.analyser;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResult;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublishedPath;

public interface PublishedPathAnalyser {
  AnalyserResult analyse(AnalyserContext ctx, PublishedPath path);
}
