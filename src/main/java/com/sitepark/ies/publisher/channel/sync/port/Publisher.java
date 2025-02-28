package com.sitepark.ies.publisher.channel.sync.port;

import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.Ref;
import java.nio.file.Path;
import java.util.List;

public interface Publisher {
  List<Publication> getPublications(Path path);

  void republish(Ref object);

  void publish(Ref object);

  void depublish(Ref object);
}
