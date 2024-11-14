package src.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

import src.exceptions.RouteMismatchException;

public class HttpRequestHandler {
  ApplicationContext applicationContext;

  public HttpRequestHandler(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  private void sendRequest(Socket socket, CustomHttpResponse response) {
    try {
      PrintWriter writer = new PrintWriter(socket.getOutputStream());
      writer.print(response);
      writer.flush();
      response.writeBody(socket.getOutputStream());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        socket.close();
      } catch (Exception e) {
      }
    }
  }

  public void handleRequests(Socket socket) {
    try {
      CustomHttpRequest request = new CustomHttpRequest(socket.getInputStream());
      String url = request.getUrl();
      MatchedRoute matchedRoute = this.applicationContext.matchRoute(url);
      boolean sentRequest = false;
      for (BeanDefinition beanDefinition : this.applicationContext.beanDefinitions) {
        if (beanDefinition.getBeanName().equals(matchedRoute.beanName)
            && beanDefinition.methodMap.containsKey(matchedRoute.totalPath)) {
          Map<HttpMethod, Method> methodMap = beanDefinition.methodMap.get(matchedRoute.totalPath);
          if (methodMap.containsKey(request.getMethodName())) {
            Method method = methodMap.get(request.getMethodName());
            try {
              sendRequest(socket, (CustomHttpResponse) method
                  .invoke(this.applicationContext.getBean(beanDefinition.getBeanName()), request));
              sentRequest = true;
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            } catch (InvocationTargetException e) {
              e.printStackTrace();
            }
          }
        }

      }
      if (!sentRequest)
        sendRequest(socket, new CustomHttpResponse(HttpStatus.NOT_FOUND));
    } catch (RouteMismatchException e) {
      sendRequest(socket, new CustomHttpResponse(HttpStatus.NOT_FOUND));
    } catch (IOException e) {
      e.printStackTrace();
      sendRequest(socket, new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR));
    }
  }
}
