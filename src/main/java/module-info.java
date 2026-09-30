import org.jspecify.annotations.NullMarked;

/**
 * This module contains the essential business logic and data structures, of the user repository.
 */
@NullMarked
module com.sitepark.ies.publisher.channel.sync {
  requires static org.jspecify;
  requires transitive com.fasterxml.jackson.databind;
}
