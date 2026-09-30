package com.sitepark.ies.publisher.channel.sync.port;

import java.nio.file.Path;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface Hasher {

  /**
   * Calculates the hash of a file.
   *
   * @return the hash of the file or <code>null</code> if the file does not exist
   */
  @Nullable String hash(Path file);
}
