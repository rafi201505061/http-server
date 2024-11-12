package utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import annotations.Controller;
import annotations.Path;

public class ClassFinder {
  public static MetaData findClasses(String packageName) {
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

  private static void processClass(String className, MetaData metaData) {
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

  private static void findClasses(File directory, String packageName, MetaData metaData) {
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
}
