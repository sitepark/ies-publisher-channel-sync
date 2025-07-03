package com.sitepark.ies.publisher.channel.sync.service.analyser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sitepark.ies.publisher.channel.sync.domain.entity.AnalyserResultFactory;
import com.sitepark.ies.publisher.channel.sync.domain.value.PublicationDirectory;
import com.sitepark.ies.publisher.channel.sync.domain.value.ResultEntryFactory;

public class AnalyserTestBase {

  protected AnalyserTestBase() {
    super();
  }

  protected AnalyserContext mockAnalyserContext() {
    AnalyserContext ctx = mock();

    PublicationDirectory publicationDirectory = mock(PublicationDirectory.class);
    when(ctx.getPublicationDirectory()).thenReturn(publicationDirectory);

    ResultEntryFactory resultEntryFactory = new ResultEntryFactory(ctx);
    when(ctx.getResultEntryFactory()).thenReturn(resultEntryFactory);

    AnalyserResultFactory analyserResultFactory = new AnalyserResultFactory(resultEntryFactory);
    when(ctx.getAnalyserResultFactory()).thenReturn(analyserResultFactory);

    return ctx;
  }
}
