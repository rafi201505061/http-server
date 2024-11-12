package utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import controllers.ControllerBase;

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
      ControllerBase controller = (ControllerBase) this.applicationContext.getBean(matchedRoute.beanName);
      sendRequest(socket, controller.handleRequest(request));
    } catch (RouteMismatchException e) {
      sendRequest(socket, new CustomHttpResponse(HttpStatus.NOT_FOUND));
    } catch (IOException e) {
      e.printStackTrace();
      sendRequest(socket, new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR));
    }
  }
}
