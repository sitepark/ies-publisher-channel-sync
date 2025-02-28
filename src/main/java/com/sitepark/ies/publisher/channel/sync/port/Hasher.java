package com.sitepark.ies.publisher.channel.sync.port;

import java.nio.file.Path;

public interface Hasher {

  /**
   * @return the hash of the file or <code>null</code> if the file does not exist
   */
  String hash(Path file);
}
