import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import controllers.BlogController;
import controllers.BlogDetailsController;
import repositories.BlogRepository;
import utils.CustomHttpRequest;
import utils.CustomHttpResponse;
import utils.HttpStatus;

public class Main {
  public static ExecutorService executorService = Executors.newCachedThreadPool();
  public static Map<String, Object> beans = new HashMap<>();

  public static void sendRequest(Socket socket, CustomHttpResponse response) {
    try {
      PrintWriter writer = new PrintWriter(socket.getOutputStream());
      writer.print(response);
      writer.print(response.getBody());
      writer.flush();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        socket.close();
      } catch (Exception e) {
      }
    }
  }

  public static void handleRequests(Socket socket) {

    try {
      CustomHttpRequest request = new CustomHttpRequest(socket.getInputStream());
      String url = request.getUrl();
      StringTokenizer stringTokenizer = new StringTokenizer(url.startsWith("/") ? url.substring(1) : url, "/");
      List<String> tokens = new ArrayList<>();
      while (stringTokenizer.hasMoreTokens()) {
        tokens.add(stringTokenizer.nextToken());
      }
      if (tokens.size() == 1 && tokens.get(0).equalsIgnoreCase("blogs")) {
        BlogController blogController = (BlogController) beans.get("blogController");
        sendRequest(socket, blogController.handleRequest(request));
      } else if (tokens.size() == 2 && tokens.get(0).equalsIgnoreCase("blogs")) {
        BlogDetailsController blogDetailsController = (BlogDetailsController) beans.get("blogDetailsController");
        sendRequest(socket, blogDetailsController.handleRequest(request));
      } else {
        sendRequest(socket, new CustomHttpResponse(HttpStatus.NOT_FOUND));
      }
    } catch (IOException e) {
      e.printStackTrace();
      sendRequest(socket, new CustomHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR));
    }
  }

  public static void main(String[] args) {
    try {
      ServerSocket serverSocket = new ServerSocket(4221);
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        try {
          serverSocket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        executorService.shutdown();
        try {
          if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
          }
        } catch (Exception e) {
          executorService.shutdownNow();
        }
      }));
      serverSocket.setReuseAddress(true);
      beans.put("blogRepository", new BlogRepository());
      beans.put("blogController", new BlogController((BlogRepository) beans.get("blogRepository")));
      beans.put("blogDetailsRepository", new BlogDetailsController((BlogRepository) beans.get("blogRepository")));

      while (true) {
        try {
          Socket clientSocket = serverSocket.accept();
          executorService.submit(() -> {
            handleRequests(clientSocket);
          });
        } catch (Exception e) {
        }

      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());

    }
  }
}
