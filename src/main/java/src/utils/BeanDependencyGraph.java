package src.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import src.exceptions.CircularDependencyException;
import src.exceptions.InvalidAutowireException;

public class BeanDependencyGraph {
  HashMap<BeanDefinition, Set<BeanDefinition>> graph = new HashMap<>();

  public BeanDependencyGraph(List<BeanDefinition> beanDefinitions) {
    Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    beanDefinitions.stream().forEach((beanDefinition) -> {
      beanDefinitionMap.put(beanDefinition.getClassName(), beanDefinition);
    });
    beanDefinitions.stream().forEach((beanDefinition) -> {
      beanDefinition.getDependencyList().stream().forEach(dependency -> {
        String dependencyClassName = dependency.getSimpleName();
        if (beanDefinitionMap.containsKey(dependencyClassName)) {
          if (!graph.containsKey(beanDefinitionMap.get(dependencyClassName))) {
            graph.put(beanDefinitionMap.get(dependencyClassName), new HashSet<>());
          }
          graph.get(beanDefinitionMap.get(dependencyClassName)).add(beanDefinition);
        } else {
          throw new InvalidAutowireException();
        }
      });
    });
  }

  public List<BeanDefinition> findTopologicalSorting() {
    List<BeanDefinition> sortedBeanDefinitions = new ArrayList<>();
    HashSet<BeanDefinition> visited = new HashSet<>();
    Stack<BeanDefinition> stck = new Stack<>();
    for (Map.Entry<BeanDefinition, Set<BeanDefinition>> entry : graph.entrySet()) {
      if (!visited.contains(entry.getKey()))
        dfs(entry.getKey(), visited, stck);
    }
    while (!stck.empty()) {
      sortedBeanDefinitions.add(stck.pop());
    }
    return sortedBeanDefinitions;
  }

  private void dfs(BeanDefinition beanDefinition, HashSet<BeanDefinition> visited, Stack<BeanDefinition> stck) {
    if (graph.containsKey(beanDefinition)) {
      visited.add(beanDefinition);
      graph.get(beanDefinition).stream().forEach(dependency -> {
        if (visited.contains(dependency)) {
          throw new CircularDependencyException();
        }
        dfs(dependency, visited, stck);
      });
      visited.remove(beanDefinition);
    }
    stck.add(beanDefinition);
  }
}
