import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import src.utils.ApplicationContext;
import src.utils.HttpRequestHandler;

public class Main {
  public static ExecutorService executorService = Executors.newCachedThreadPool();

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
      ApplicationContext applicationContext = null;
      try {
        applicationContext = new ApplicationContext();
      } catch (Exception e) {
        e.printStackTrace();
      }
      HttpRequestHandler httpRequestHandler = new HttpRequestHandler(applicationContext);
      while (true) {
        try {
          Socket clientSocket = serverSocket.accept();
          System.out.println("Accepted New Socket Connection");

          executorService.submit(() -> {
            httpRequestHandler.handleRequests(clientSocket);
          });
        } catch (Exception e) {
          System.out.println("Closed Socket");
          e.printStackTrace();
        }

      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());

    }
  }
}
