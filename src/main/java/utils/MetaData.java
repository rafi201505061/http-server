package utils;

import java.util.ArrayList;
import java.util.List;

public class MetaData {
  List<String> controllerClassNames;
  List<String> repositoryClassNames;
  List<String> paths;

  public MetaData() {
    controllerClassNames = new ArrayList<>();
    repositoryClassNames = new ArrayList<>();
    paths = new ArrayList<>();
  }

  public void addController(String className) {
    this.controllerClassNames.add(className);
  }

  public void addRepository(String className) {
    this.repositoryClassNames.add(className);
  }

  public void addPath(String path) {
    this.paths.add(path);
  }

  public List<String> getControllerClassNames() {
    return controllerClassNames;
  }

  public List<String> getRepositoryClassNames() {
    return repositoryClassNames;
  }

  public List<String> getPaths() {
    return paths;
  }

  @Override
  public String toString() {
    return "MetaData [controllerClassNames=" + controllerClassNames + ", repositoryClassNames=" + repositoryClassNames
        + ", paths=" + paths + "]";
  }
}
