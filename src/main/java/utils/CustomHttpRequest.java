package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class CustomHttpRequest {
  private HttpMethod method;
  private String httpVersion;
  private String url;
  private Map<String, String> headers = new HashMap<>();
  private char[] body;

  private HttpMethod parseHttpMethod(String method) throws InvalidMethodException {
    switch (method.toLowerCase().trim()) {
      case "get":
        return HttpMethod.GET;
      case "post":
        return HttpMethod.POST;
      case "put":
        return HttpMethod.PUT;
      case "patch":
        return HttpMethod.PATCH;
      case "delete":
        return HttpMethod.DELETE;
      default:
        throw new InvalidMethodException();
    }
  }

  public CustomHttpRequest(InputStream inputStream) throws IOException, InvalidMethodException {
    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
    String requestLine = in.readLine();
    StringTokenizer requestLineTokenizer = new StringTokenizer(requestLine, " ");
    method = parseHttpMethod(requestLineTokenizer.nextToken());
    url = requestLineTokenizer.nextToken();
    httpVersion = requestLineTokenizer.nextToken();
    System.out.println("**** Request Line ****");
    System.out.println();
    System.out.println(method + " " + url + " " + httpVersion);
    while (true) {
      String headerLine = in.readLine();
      // end of headers
      if (headerLine.isEmpty())
        break;

      StringTokenizer headerLineTokenizer = new StringTokenizer(headerLine, ":");
      String header = headerLineTokenizer.nextToken().trim().toLowerCase();
      headers.put(header, headerLineTokenizer.nextToken().trim());
    }
    System.out.println("\n\n");
    System.out.println("**** headers ****");
    System.out.println();

    for (Map.Entry<String, String> entry : headers.entrySet()) {
      System.out.println(entry.getKey() + ": " + entry.getValue());
    }
    System.out.println("\n\n");

    if (headers.containsKey("content-type") && ContentType.fromValue(headers.get("content-type")) != null
        && headers.containsKey("content-length")) {
      int contentLength = Integer.parseInt(headers.get("content-length"));
      body = new char[contentLength];
      in.read(body, 0, contentLength);
    }
    // System.out.println("**** body ****");
    // System.out.println(new String(body));

  }

  public HttpMethod getMethodName() {
    return method;
  }

  public String getHttpVersion() {
    return httpVersion;
  }

  public String getUrl() {
    return url;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public char[] getBody() {
    return body;
  }

}
