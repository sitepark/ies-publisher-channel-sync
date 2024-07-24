package com.sitepark.ies.publisher.channel.sync.service;

import com.sitepark.ies.publisher.channel.sync.domain.entity.Publication;
import com.sitepark.ies.publisher.channel.sync.domain.entity.PublicationDirectory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PublicationDirectoryTreeBuilder {

  private final Node root = new Node("");

  public void add(Publication publication) {
    Objects.requireNonNull(publication, "publication is required");

    Path path = this.toPath(publication);
    Node node = this.getNode(path);
    node.publications.add(publication);
  }

  private Path toPath(Publication publication) {

    Path path = publication.path();

    Objects.requireNonNull(path, "path is required");

    if (!publication.isCollision()) {
      return path;
    }

    Path parent = path.getParent();
    if (parent == null) {
      return path;
    }

    if (!parent.endsWith("_" + publication.object().id())) {
      return path;
    }

    Path base = parent.getParent();
    if (base == null) {
      base = Path.of("");
    }

    return base.resolve(path.getFileName());
  }

  public PublicationDirectory build() {
    return this.build(this.root);
  }

  private PublicationDirectory build(Node node) {
    PublicationDirectory.Builder builder = PublicationDirectory.builder();
    if (!node.name.isEmpty()) {
      builder.name(node.name);
    }
    builder.publications(node.publications);
    builder.children(this.buildChildren(node));
    return builder.build();
  }

  private List<PublicationDirectory> buildChildren(Node node) {

    List<PublicationDirectory> children = new ArrayList<>();
    for (Node child : node.children.values()) {
      PublicationDirectory directory = this.build(child);
      children.add(directory);
    }
    return children;
  }

  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  private Node getNode(Path path) {

    Path parent = path.getParent();
    if (parent == null) {
      return this.root;
    }

    Node node = this.root;

    for (Path dir : parent) {
      String name = dir.toString();
      Node child = node.children.get(name);
      if (child == null) {
        child = new Node(name);
        node.children.put(name, child);
      }
      node = child;
    }

    return node;
  }

  private static class Node {

    private final String name;

    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private final Map<String, Node> children = new HashMap<>();

    private final List<Publication> publications = new ArrayList<>();

    public Node(String name) {
      Objects.requireNonNull(name, "name is required");
      this.name = name;
    }
  }
}
