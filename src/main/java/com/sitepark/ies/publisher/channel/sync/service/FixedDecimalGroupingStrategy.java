package com.sitepark.ies.publisher.channel.sync.service;

import java.nio.file.Path;

/**
 * Fixed-depth decimal grouping strategy: - groups of N decimal digits (base 10^N) - exactly
 * (levels) directories + 1 filename group - left padded with zeros - optional extension for
 * filename
 *
 * <p>Example levels=2, digitsPerLevel=3: 0 -> 000/000/000.[extension] 1000 ->
 * 000/001/000.[extension]
 */
public final class FixedDecimalGroupingStrategy implements IdPathStrategy {

  private final int levels;
  private final int digitsPerLevel;
  private final long base;

  public FixedDecimalGroupingStrategy(int levels, int digitsPerLevel) {
    if (levels < 0) throw new IllegalArgumentException("levels must be >= 0");
    if (digitsPerLevel <= 0) throw new IllegalArgumentException("digitsPerLevel must be > 0");
    this.levels = levels;
    this.digitsPerLevel = digitsPerLevel;
    this.base = pow10Long(digitsPerLevel);
  }

  private static String leftPad(long n, int width) {
    String s = Long.toString(n);
    if (s.length() >= width) return s;
    return "0".repeat(width - s.length()) + s;
  }

  private static long pow10Long(int exp) {
    long r = 1L;
    for (int i = 0; i < exp; i++) {
      r = Math.multiplyExact(r, 10L);
    }
    return r;
  }

  private static long safePowLong(long base, int exp) {
    long r = 1L;
    for (int i = 0; i < exp; i++) {
      r = Math.multiplyExact(r, base);
    }
    return r;
  }

  @Override
  public Path toPath(long id) {

    this.validate(id);

    int parts = levels + 1;
    long[] groups = new long[parts];
    long value = id;

    for (int i = parts - 1; i >= 0; i--) {
      groups[i] = value % base;
      value /= base;
    }

    Path p = Path.of(leftPad(groups[0], digitsPerLevel));
    for (int i = 1; i < levels; i++) {
      p = p.resolve(leftPad(groups[i], digitsPerLevel));
    }

    String file = leftPad(groups[parts - 1], digitsPerLevel);
    return p.resolve(file);
  }

  private void validate(long id) {

    if (id < 0) throw new IllegalArgumentException("id must be >= 0");

    long cap = safePowLong(base, levels + 1); // base^(levels+1)
    long max = cap - 1;

    if (max >= 0 && id > max) {
      throw new IllegalArgumentException(
          "id must be <= "
              + max
              + " for fixed layout (levels="
              + levels
              + ", digits="
              + digitsPerLevel
              + ")");
    }
  }
}
