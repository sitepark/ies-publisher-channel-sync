/**
 * This module contains the essential business logic
 * and data structures, of the user repository.
 */
module com.sitepark.ies.publisher.channel.sync {
  requires org.apache.logging.log4j;
  requires com.github.spotbugs.annotations;
  requires jakarta.inject;
  requires transitive com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.datatype.jdk8;
  requires com.fasterxml.jackson.datatype.jsr310;
  requires org.eclipse.jdt.annotation;
}
