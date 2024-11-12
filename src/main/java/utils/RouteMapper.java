package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class RouteMapper {
  private RouteMapperTrieNode root;

  public RouteMapper(Map<String, List<String>> pathMap) {
    System.out.println("Path Map = " + pathMap);
    this.root = new RouteMapperTrieNode();
    for (Map.Entry<String, List<String>> entry : pathMap.entrySet()) {
      String packageName = entry.getKey();
      String className = packageName.substring(packageName.lastIndexOf(".") + 1);
      List<String> paths = entry.getValue();
      String beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
      for (String path : paths) {
        insert(path, beanName);
      }
    }
  }

  public MatchedRoute matchRoute(String path) {
    MatchedRoute matchedRoute = new MatchedRoute();
    Map<String, String> pathVariableMap = new HashMap<>();
    List<String> tokens = tokenizePath(path);
    RouteMapperTrieNode curr = this.root;
    for (int i = 0; i < tokens.size(); i++) {
      String token = tokens.get(i);
      RouteMapperTrieNode foundNode = curr.findWithVariable(token);

      if (foundNode == null)
        throw new RouteMismatchException();
      if (foundNode.isVariable) {
        pathVariableMap.put(foundNode.variableName, token);
      }
      curr = foundNode;
    }
    if (curr.isFullRoute) {
      matchedRoute.beanName = curr.controllerBeanName;
      matchedRoute.pathMap = pathVariableMap;
    } else {
      throw new RouteMismatchException();
    }
    return matchedRoute;
  }

  private List<String> tokenizePath(String path) {
    StringTokenizer stringTokenizer = new StringTokenizer(path, "/");
    List<String> tokens = new ArrayList<>();
    while (stringTokenizer.hasMoreTokens()) {
      String token = stringTokenizer.nextToken();
      if (!token.isBlank()) {
        tokens.add(token);
      }
    }
    return tokens;
  }

  private void insert(String path, String beanName) {
    List<String> tokens = tokenizePath(path);
    RouteMapperTrieNode curr = this.root;
    for (int i = 0; i < tokens.size(); i++) {
      String token = tokens.get(i);
      curr = curr.add(token);
      if (i == tokens.size() - 1) {
        curr.controllerBeanName = beanName;
        curr.isFullRoute = true;
      }
    }
  }

  public void print() {
    List<RouteMapperTrieNode> currList = new ArrayList<>();
    List<RouteMapperTrieNode> nextList = new ArrayList<>();
    currList.add(root);
    while (!currList.isEmpty()) {
      StringBuilder sb = new StringBuilder();

      for (RouteMapperTrieNode curr : currList) {
        sb.append("(");
        sb.append(curr.name);
        sb.append(",");
        sb.append(curr.isFullRoute);
        sb.append(")    -----    ");
        for (RouteMapperTrieNode temp : curr.children) {
          nextList.add(temp);
        }
      }
      currList = nextList;
      nextList = new ArrayList<>();
      System.out.println(sb.toString());
    }
  }

}
