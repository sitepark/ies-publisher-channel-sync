package com.sitepark.ies.publisher.channel.sync.domain.entity;

import java.nio.file.Files;
import java.nio.file.Path;

public record PublishedPath(PublicationType type, Path absolutePath, String baseName) {
  public boolean isDirectory() {
    return Files.isDirectory(this.absolutePath());
  }
}
