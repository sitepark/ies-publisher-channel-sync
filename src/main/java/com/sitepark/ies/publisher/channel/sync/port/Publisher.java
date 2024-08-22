package com.sitepark.ies.publisher.channel.sync.port;

import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Ref;
import java.nio.file.Path;
import java.util.List;

public interface Publisher {
  public List<Publication> getPublications(Path path);

  public void republish(Ref object);

  public void publish(Ref object);

  public void depublish(Ref object);
}
