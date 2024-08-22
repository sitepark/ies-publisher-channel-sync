package com.sitepark.ies.publisher.channel.sync.domain.entity;

public enum ResultType {
  UNKNOWN_FILE_OR_DIRECTORY('?', "Unknown file or directory"),

  FILE_DIRECTORY_MISMATCH('~', "file / directory mismatch"),

  PUBLICATION_TYPE_MISMATCH('t', "publication type mismatch"),

  MISSING_FILE('!', "missing file"),

  HASH_MISMATCH('H', "hash mismatch"),

  ILLEGAL_FILENAME_COLLISION('C', "illegal filename collision"),

  LEGAL_FILENAME_COLLISION('c', "illegal filename collision"),

  LOST_PUBLICATION('P', "lost publication"),

  TEMPLATE_MISSING('T', "template missing");

  private final char symbol;

  private final String description;

  private ResultType(char symbol, String description) {
    this.symbol = symbol;
    this.description = description;
  }

  public char getSymbol() {
    return symbol;
  }

  public String getDescription() {
    return description;
  }
}
