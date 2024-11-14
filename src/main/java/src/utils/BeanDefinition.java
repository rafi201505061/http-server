package src.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import src.annotations.Autowired;
import src.exceptions.MultipleAutowiredConstructors;

public class BeanDefinition {

  private BeanType type;
  private String className;
  private String classPath;
  private String beanName;
  private Method[] methods;
  private Field[] fields;
  private Constructor<?>[] constructors;
  public Map<String, Map<HttpMethod, Method>> methodMap;
  public List<String> paths;
  private Class<?> cls;
  private Set<Class<?>> dependencyList = new HashSet<>();
  private Constructor<?> candidateConstructor = null;

  public BeanDefinition(BeanType type, String classPath, Class<?> cls) {
    this.type = type;
    methodMap = new HashMap<>();
    methods = new Method[0];
    paths = new ArrayList<>();
    constructors = new Constructor[0];
    this.classPath = classPath;
    int lastDotIndex = classPath.lastIndexOf(".");
    this.className = classPath.substring(lastDotIndex == -1 ? 0 : lastDotIndex + 1);
    this.beanName = this.className.substring(0, 1).toLowerCase() + this.className.substring(1);
    this.cls = cls;
  }

  public BeanType getType() {
    return type;
  }

  public String getClassName() {
    return className;
  }

  public String getBeanName() {
    return beanName;
  }

  public String getClassPath() {
    return classPath;
  }

  public Class<?> getCls() {
    return cls;
  }

  public Method[] getMethods() {
    return methods;
  }

  public void setMethods(Method[] methods) {
    this.methods = methods;
  }

  public Constructor<?>[] getConstructors() {
    return constructors;
  }

  public Field[] getFields() {
    return fields;
  }

  public void setFields(Field[] fields) {
    this.fields = fields;
    for (Field field : this.fields) {
      if (field.isAnnotationPresent(Autowired.class)) {
        this.dependencyList.add(field.getClass());
      }
    }
  }

  public void setConstructors(Constructor<?>[] constructors) {
    this.constructors = constructors;
    int candidateConstructors = 0;
    Constructor<?> constructorWithMaxParams = null;
    for (Constructor<?> constructor : this.constructors) {
      if (constructorWithMaxParams == null) {
        constructorWithMaxParams = constructor;
      } else {
        if (constructorWithMaxParams.getParameterTypes().length < constructor.getParameterTypes().length) {
          constructorWithMaxParams = constructor;
        }
      }
      if (constructor.isAnnotationPresent(Autowired.class)) {
        this.candidateConstructor = constructor;
        candidateConstructors++;
        this.dependencyList.addAll(Arrays.asList(constructor.getParameterTypes()));
      }
    }
    if (this.candidateConstructor == null) {
      this.candidateConstructor = constructorWithMaxParams;
    }
    if (candidateConstructors > 1) {
      throw new MultipleAutowiredConstructors();
    }
  }

  public Set<Class<?>> getDependencyList() {
    return dependencyList;
  }

  @Override
  public String toString() {
    return "BeanDefinition [className=" + className + ", dependencyList=" + dependencyList + "]";
  }

  public Constructor<?> getCandidateConstructor() {
    return candidateConstructor;
  }

}
