package utils;

import java.util.HashMap;
import java.util.Map;

import controllers.BlogController;
import controllers.BlogDetailsController;
import repositories.BlogRepository;

public class ApplicationContext {
  Map<String, Object> beans = new HashMap<>();
  MetaData metadata;
  RouteMapper routeMapper;

  public ApplicationContext() {
    metadata = ClassFinder.findClasses("controllers");
    routeMapper = new RouteMapper(metadata.controllerPathMapper);
    beans.put("blogRepository", new BlogRepository());
    beans.put("blogController", new BlogController((BlogRepository) beans.get("blogRepository")));
    beans.put("blogDetailsRepository", new BlogDetailsController((BlogRepository) beans.get("blogRepository")));
    routeMapper.print();
  }

  public MatchedRoute matchRoute(String path) {
    return this.routeMapper.matchRoute(path);
  }

  public Object getBean(String beanName) {
    return beans.getOrDefault(beanName, null);
  }
}
