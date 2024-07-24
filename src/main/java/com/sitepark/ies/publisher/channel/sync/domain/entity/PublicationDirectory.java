package com.sitepark.ies.publisher.channel.sync.domain.entity;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class PublicationDirectory {

  private final String name;

  private PublicationDirectory parent;

  private final Map<String, PublicationDirectory> children;

  private final Map<String, List<Publication>> publications;

  private final Map<String, Publication> collisions;

  private PublicationDirectory(
      String name,
      Map<String, PublicationDirectory> children,
      Map<String, List<Publication>> publications,
      Map<String, Publication> collisions) {
    this.name = name;
    for (PublicationDirectory child : children.values()) {
      child.parent = this;
    }
    this.children = Collections.unmodifiableMap(children);
    this.publications = Collections.unmodifiableMap(publications);
    this.collisions = Collections.unmodifiableMap(collisions);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(this.name, this.children, this.publications, this.collisions);
  }

  @Override
  public final boolean equals(Object o) {
    return (o instanceof PublicationDirectory PublicationDirectory)
        && Objects.equals(this.name, PublicationDirectory.name)
        && Objects.equals(this.children, PublicationDirectory.children)
        && Objects.equals(this.publications, PublicationDirectory.publications)
        && Objects.equals(this.collisions, PublicationDirectory.collisions);
  }

  public static Builder builder() {
    return new Builder();
  }

  public Builder toBuilder() {
    return new Builder()
        .name(this.name)
        .children(this.children.values())
        .publications(
            this.publications.values().stream()
                .flatMap(T -> T.stream())
                .collect(Collectors.toList()));
  }

  public String getName() {
    return this.name;
  }

  public PublicationDirectory getParent() {
    return this.parent;
  }

  public String getPath() {
    StringBuilder path = new StringBuilder();
    PublicationDirectory d = this;
    while (d != null) {
      if (d.getName() != null) {
        path.insert(0, d.getName());
      }
      d = d.getParent();
    }
    return path.toString();
  }

  public List<Publication> getPublications(boolean recursive) {
    List<Publication> list = new ArrayList<Publication>();

    if (this.publications != null) {
      for (List<Publication> l : this.publications.values()) {
        list.addAll(l);
      }
    }
    if (recursive && this.children != null) {
      for (PublicationDirectory child : this.children.values()) {
        list.addAll(child.getPublications(true));
      }
    }
    return list;
  }

  public Collection<String> getPublicationFileNames() {
    if (this.publications == null) {
      return new ArrayList<>();
    } else {
      return this.publications.keySet();
    }
  }

  public List<Publication> getPublications(String fileName) {
    if (this.publications == null) {
      return Collections.emptyList();
    }

    List<Publication> publications = this.publications.get(fileName);
    if (publications == null) {
      return Collections.emptyList();
    }

    return publications;
  }

  public boolean hasPublications(String fileName) {
    return !this.getPublications(fileName).isEmpty();
  }

  public Collection<PublicationDirectory> getChildren() {
    if (this.children == null) {
      return Collections.emptyList();
    } else {
      return this.children.values();
    }
  }

  public PublicationDirectory getChild(String name) {
    if (this.children == null) {
      return null;
    } else {
      return this.children.get(name);
    }
  }

  public PublicationDirectory findChild(Path path) {
    if (this.children == null) {
      return null;
    } else {
      PublicationDirectory directory = this;
      for (Path name : path) {
        directory = directory.getChild(name.toString());
        if (directory == null) {
          return null;
        }
      }
      return directory;
    }
  }

  public Publication getCollision(String name) {
    return this.collisions.get(name);
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder(this.getPath());
    if (!this.publications.isEmpty()) {
      StringBuilder pb = new StringBuilder();
      for (List<Publication> list : this.publications.values()) {
        for (Publication publication : list) {
          if (!pb.isEmpty()) {
            pb.append(", ");
          }
          pb.append(publication.path()).append(" (").append(publication.object()).append(')');
        }
      }
      b.append(", publications:(").append(pb).append(')');
    }
    if (!this.children.isEmpty()) {
      StringBuilder cb = new StringBuilder();
      for (PublicationDirectory child : this.children.values()) {
        if (!cb.isEmpty()) {
          cb.append(", ");
        }
        cb.append(child.toString());
      }
      b.append(", children:(").append(cb).append(')');
    }
    return b.toString();
  }

  @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
  public static final class Builder {

    private String name;

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private final Map<String, PublicationDirectory> children =
        new HashMap<String, PublicationDirectory>();

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private final Map<String, List<Publication>> publications =
        new HashMap<String, List<Publication>>();

    public Builder name(String name) {

      if (name == null) {
        return this;
      }

      if (name.isBlank()) {
        throw new IllegalArgumentException("PublicationDirectory name must not be blank");
      }

      if ((name.startsWith("/"))) {
        throw new IllegalArgumentException(
            "PublicationDirectory name must not start with '/': " + name);
      } else if (name.endsWith("/")) {
        throw new IllegalArgumentException(
            "PublicationDirectory name must not ends with '/': " + name);
      }
      this.name = name;
      return this;
    }

    public Builder publications(Collection<Publication> publicatios) {
      Objects.requireNonNull(publicatios, "Publications must not be null");
      for (Publication publication : publicatios) {
        this.publication(publication);
      }
      return this;
    }

    public Builder publication(Publication publication) {
      Objects.requireNonNull(publication, "Publication must not be null");
      String fileName = publication.fileName();
      List<Publication> list = this.publications.get(fileName);
      if (list == null) {
        list = new ArrayList<Publication>();
        this.publications.put(fileName, list);
      }
      list.add(publication);
      this.publications.put(fileName, list);
      return this;
    }

    public Builder children(Collection<PublicationDirectory> children) {
      Objects.requireNonNull(children, "Children must not be null");
      for (PublicationDirectory child : children) {
        this.child(child);
      }
      return this;
    }

    public Builder child(PublicationDirectory child) {
      Objects.requireNonNull(child, "Child must not be null");
      this.children.put(child.getName(), child);
      return this;
    }

    public PublicationDirectory build() {

      @SuppressWarnings("PMD.UseConcurrentHashMap")
      Map<String, Publication> collisions = new HashMap<String, Publication>();

      this.publications.values().stream()
          .flatMap(list -> list.stream())
          .filter(p -> p.isCollision())
          .forEach(
              p -> {
                collisions.put("_" + p.object().id(), p);
              });

      return new PublicationDirectory(this.name, this.children, this.publications, collisions);
    }
  }
}
