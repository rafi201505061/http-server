package src.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import src.annotations.Controller;
import src.annotations.Delete;
import src.annotations.Get;
import src.annotations.Patch;
import src.annotations.Path;
import src.annotations.Post;
import src.annotations.Put;
import src.annotations.Repository;
import src.exceptions.InvalidBeanDefinitionException;
import src.exceptions.MultipleHttpMethodException;

public class ClassFinder {
  public MetaData findClasses(String packageName) {
    MetaData metaData = new MetaData();

    try {
      String packagePath = packageName.replace('.', '/');
      Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
      while (resources.hasMoreElements()) {
        URL resource = resources.nextElement();
        File file = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
        findClasses(file, packageName, metaData);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return metaData;
  }

  private void processClass(String className, MetaData metaData) {
    try {
      Class<?> cls = Class.forName(className);
      if (cls.isAnnotationPresent(Controller.class)) {
        metaData.addController(className);
        if (cls.isAnnotationPresent(Path.class)) {
          List<String> paths = new ArrayList<>();
          String rootPath = cls.getAnnotation(Path.class).value();
          paths.add(rootPath);
          for (Method method : cls.getMethods()) {
            if (method.isAnnotationPresent(Path.class)) {
              paths.add(rootPath + method.getAnnotation(Path.class).value());
            }
          }
          metaData.addToControllerPathMapper(className, paths);
        }
      }
    } catch (Exception e) {
      System.out.println("PARSE ERROR: " + className);
      e.printStackTrace();
    }

  }

  private void findClasses(File directory, String packageName, MetaData metaData) {
    if (directory.isDirectory()) {
      for (File file : directory.listFiles()) {
        if (file.isDirectory()) {
          findClasses(file, packageName + "." + file.getName(), metaData);
        } else if (file.getName().endsWith(".class")) {
          String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
          processClass(className, metaData);
        }
      }
    }
  }

  public List<BeanDefinition> findBeanDefinitions(String packageName) {
    List<BeanDefinition> beanDefinitions = new ArrayList<>();
    try {
      String packagePath = packageName.replace('.', '/');
      Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
      while (resources.hasMoreElements()) {
        URL resource = resources.nextElement();
        File file = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
        findBeanDefinitions(file, packageName, beanDefinitions);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return beanDefinitions;
  }

  private BeanDefinition getBeanDefinition(String classPath) throws ClassNotFoundException {
    Class<?> cls = Class.forName(classPath);
    if (cls.isAnnotationPresent(Controller.class) || cls.isAnnotationPresent(Repository.class)) {
      BeanDefinition beanDefinition = new BeanDefinition(cls.isAnnotationPresent(Controller.class) ? BeanType.CONTROLLER
          : cls.isAnnotationPresent(Repository.class) ? BeanType.REPOSITORY : null, classPath, cls);
      beanDefinition.setMethods(cls.getDeclaredMethods());
      beanDefinition.setConstructors(cls.getConstructors());
      beanDefinition.setFields(cls.getDeclaredFields());
      if (cls.isAnnotationPresent(Controller.class)) {
        String basePath = cls.isAnnotationPresent(Path.class) ? cls.getAnnotation(Path.class).value() : "";
        if (!basePath.isBlank())
          beanDefinition.paths.add(basePath);
        for (Method method : beanDefinition.getMethods()) {
          String totalPath = basePath;
          if (method.isAnnotationPresent(Path.class)) {
            String path = method.getAnnotation(Path.class).value();

            if (path.startsWith("/") && basePath.endsWith("/")) {
              totalPath = basePath + path.substring(1);
            } else {
              totalPath = basePath + path;
            }
            beanDefinition.paths.add(totalPath);
          }
          if (isHttpMethodAnnotationPresent(method)) {

            if (!beanDefinition.methodMap.containsKey(totalPath)) {
              beanDefinition.methodMap.put(totalPath, new HashMap<>());
            }
            if (beanDefinition.methodMap.get(totalPath).containsKey(mapAnnotationToEnum(method))) {
              throw new MultipleHttpMethodException("Duplicate Request Handler");
            } else
              beanDefinition.methodMap.get(totalPath).put(mapAnnotationToEnum(method), method);
          }
        }
      }
      return beanDefinition;
    } else {
      throw new InvalidBeanDefinitionException();
    }
  }

  private boolean isHttpMethodAnnotationPresent(Method method) {
    int numAnnotationsPresent = 0;
    if (method.isAnnotationPresent(Post.class)) {
      numAnnotationsPresent++;
    }
    if (method.isAnnotationPresent(Put.class)) {
      numAnnotationsPresent++;

    }
    if (method.isAnnotationPresent(Get.class)) {
      numAnnotationsPresent++;

    }
    if (method.isAnnotationPresent(Patch.class)) {
      numAnnotationsPresent++;

    }
    if (method.isAnnotationPresent(Delete.class)) {
      numAnnotationsPresent++;
    }
    if (numAnnotationsPresent > 1) {
      throw new MultipleHttpMethodException();
    }
    return numAnnotationsPresent == 1;
  }

  private HttpMethod mapAnnotationToEnum(Method method) {
    if (method.isAnnotationPresent(Post.class)) {
      return HttpMethod.POST;
    } else if (method.isAnnotationPresent(Put.class)) {
      return HttpMethod.PUT;
    } else if (method.isAnnotationPresent(Get.class)) {
      return HttpMethod.GET;
    } else if (method.isAnnotationPresent(Patch.class)) {
      return HttpMethod.PATCH;
    } else if (method.isAnnotationPresent(Delete.class)) {
      return HttpMethod.DELETE;
    } else
      return null;
  }

  private void findBeanDefinitions(File directory, String packageName, List<BeanDefinition> beans) {
    if (directory.isDirectory()) {
      for (File file : directory.listFiles()) {
        if (file.isDirectory()) {
          findBeanDefinitions(file, packageName + "." + file.getName(), beans);
        } else if (file.getName().endsWith(".class")) {
          String classPath = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
          try {
            BeanDefinition beanDefinition = getBeanDefinition(classPath);
            beans.add(beanDefinition);
          } catch (Exception e) {

          }
        }
      }
    } else if (directory.getName().endsWith(".class")) {
      String classPath = packageName + '.' + directory.getName().substring(0, directory.getName().length() - 6);
      try {
        BeanDefinition beanDefinition = getBeanDefinition(classPath);
        beans.add(beanDefinition);
      } catch (Exception e) {
      }
    }
  }
}
