package utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Objects;

import annotations.Controller;
import annotations.Path;

public class ClassFinder {
  public static MetaData findClasses(String packageName) throws IOException, ClassNotFoundException {
    MetaData metaData = new MetaData();

    String packagePath = packageName.replace('.', '/');
    Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      File file = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
      System.out.println("item = " + resource.getFile() + "  dff = " + file.isDirectory() + "  ddd = " + file.isFile());

      if (file.isDirectory()) {
        System.out.println("directory = " + resource.getFile());

        findClassesInDirectory(file, packageName, metaData);
      } else if (file.getName().endsWith(".class")) {
        String className = packageName + "." + file.getName().substring(0,
            file.getName().length() - 6);
        processClass(className, metaData);
      }
    }
    return metaData;
  }

  private static void processClass(String className, MetaData metaData) throws ClassNotFoundException {
    System.out.println("className = " + className);

    Class<?> cls = Class.forName(className);
    if (cls.isAnnotationPresent(Controller.class)) {
      System.out.println("controller");

      metaData.addController(className);
      if (cls.isAnnotationPresent(Path.class)) {
        String rootPath = cls.getAnnotation(Path.class).value();
        metaData.addPath(rootPath);
        for (Method method : cls.getMethods()) {
          if (method.isAnnotationPresent(Path.class)) {
            metaData.addPath(rootPath + method.getAnnotation(Path.class).value());
          }
        }
      }
    }
  }

  private static void findClassesInDirectory(File directory, String packageName, MetaData metaData)
      throws ClassNotFoundException {

    for (File file : Objects.requireNonNull(directory.listFiles())) {
      if (file.isDirectory()) {
        System.out.println("directory = " + file.getName());

        findClassesInDirectory(file, packageName + "." + file.getName(), metaData);
      } else if (file.getName().endsWith(".class")) {
        String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
        processClass(className, metaData);
      }
    }
  }
}
