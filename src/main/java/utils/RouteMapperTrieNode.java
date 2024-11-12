package utils;

import java.util.ArrayList;
import java.util.List;

public class RouteMapperTrieNode {

  public boolean isVariable = false;
  public String name = "/";
  public String variableName = "";
  public boolean isFullRoute = false;
  public String controllerBeanName = "";

  public List<RouteMapperTrieNode> children = new ArrayList<>();

  public RouteMapperTrieNode() {
  }

  public RouteMapperTrieNode(String name) {
    this.name = name;
  }

  public boolean match(String name) {
    return this.name.equals(name);
  }

  public RouteMapperTrieNode find(String token) {
    for (RouteMapperTrieNode child : children) {
      if (child.match(token)) {
        return child;
      }
    }
    return null;
  }

  public boolean matchWithVariable(String name) {
    return this.isVariable || this.name.equals(name);
  }

  public RouteMapperTrieNode findWithVariable(String token) {
    for (RouteMapperTrieNode child : children) {
      if (child.matchWithVariable(token)) {
        return child;
      }
    }
    return null;
  }

  public RouteMapperTrieNode add(String token) {
    RouteMapperTrieNode foundNode = find(token);
    if (foundNode == null) {
      foundNode = new RouteMapperTrieNode(token);
      this.children.add(foundNode);
    }
    if (token.startsWith("{") && token.endsWith("}")) {
      foundNode.isVariable = true;
      foundNode.variableName = token.substring(1, token.length() - 1).trim();
    }
    return foundNode;
  }

  @Override
  public String toString() {
    return "RouteMapperTrieNode [isVariable=" + isVariable + ", name=" + name + ", variableName=" + variableName
        + ", isFullRoute=" + isFullRoute + ", controllerBeanName=" + controllerBeanName + ", children=" + children
        + "]";
  }

}
