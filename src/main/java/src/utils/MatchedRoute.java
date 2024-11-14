package src.utils;

import java.util.HashMap;
import java.util.Map;

public class MatchedRoute {
  String beanName = null;
  String totalPath = "";
  Map<String, String> pathMap = new HashMap<>();

  @Override
  public String toString() {
    return "MatchedRoute [beanName=" + beanName + ", totalPath=" + totalPath + ", pathMap=" + pathMap + "]";
  }
}
