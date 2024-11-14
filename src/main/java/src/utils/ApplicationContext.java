package src.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.exceptions.CircularDependencyException;

public class ApplicationContext {
  Map<String, Object> beans = new HashMap<>();
  RouteMapper routeMapper;
  List<BeanDefinition> beanDefinitions;

  public ApplicationContext() {
    // find all bean candidates and their dependency list
    beanDefinitions = new ClassFinder().findBeanDefinitions("src");

    // validate and build dependency list
    try {
      // initialize beans
      BeanDependencyGraph beanDependencyGraph = new BeanDependencyGraph(beanDefinitions);
      List<BeanDefinition> beanInitializationOrder = beanDependencyGraph.findTopologicalSorting();

      beanInitializationOrder.stream().forEach(beanDefinition -> {
        try {
          Constructor<?> constructor = beanDefinition.getCandidateConstructor();
          beans.put(beanDefinition.getBeanName(),
              constructor.newInstance(Arrays.asList(constructor.getParameterTypes()).stream().map(param -> {
                String beanName = param.getSimpleName().substring(0, 1).toLowerCase()
                    + param.getSimpleName().substring(1);
                return beans.get(beanName);
              }).toArray()));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
            | SecurityException e) {
          e.printStackTrace();
        }
      });

      // build route matcher trie
      routeMapper = new RouteMapper(beanDefinitions);

    } catch (CircularDependencyException e) {
      throw e;
    }

  }

  public MatchedRoute matchRoute(String path) {
    return this.routeMapper.matchRoute(path);
  }

  public Object getBean(String beanName) {
    return beans.getOrDefault(beanName, null);
  }
}
