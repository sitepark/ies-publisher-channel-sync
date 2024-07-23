package com.sitepark.ies.publisher.channel.sync.domain.entity;

import java.nio.file.Path;

public enum PublicationType {
  OBJECT("objects"),
  PUBLIC_MEDIA("media/public"),
  PROTECTED_MEDIA("media/protected"),
  REDIRECT_RULES("redirects"),
  SECURITY_RULES("security"),
  SECURITY_USERS("security"),
  CONFIG("configs");

  private final Path path;

  private PublicationType(String path) {
    this.path = Path.of(path);
  }

  public Path getPath() {
    return this.path;
  }
}
