package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaData {

  public List<String> controllerClassNames;
  public List<String> repositoryClassNames;
  public Map<String, List<String>> controllerPathMapper;

  public MetaData() {
    controllerClassNames = new ArrayList<>();
    repositoryClassNames = new ArrayList<>();
    controllerPathMapper = new HashMap<>();
  }

  public void addController(String className) {
    this.controllerClassNames.add(className);
  }

  public void addRepository(String className) {
    this.repositoryClassNames.add(className);
  }

  public void addToControllerPathMapper(String className, List<String> paths) {
    this.controllerPathMapper.put(className, paths);
  }

  @Override
  public String toString() {
    return "MetaData [controllerClassNames=" + controllerClassNames + ", repositoryClassNames=" + repositoryClassNames
        + ", controllerPathMapper=" + controllerPathMapper + "]";
  }

}
