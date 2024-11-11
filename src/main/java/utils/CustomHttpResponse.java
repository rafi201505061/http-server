package utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class CustomHttpResponse {
  private String httpVersion = "HTTP/1.1";
  private HttpStatus status;
  Map<String, String> headers = new HashMap<>();
  private byte[] body;

  public byte[] getBody() {
    return body;
  }

  public CustomHttpResponse(HttpStatus status) {
    this.status = status;
  }

  CustomHttpResponse(HttpStatus status, byte[] body) {
    this.status = status;
    this.body = body;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    // status line
    sb.append(httpVersion);
    sb.append(" ");
    sb.append(status.getValue());
    sb.append("\r\n");

    // headers
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      sb.append(entry.getKey());
      sb.append(": ");
      sb.append(entry.getValue());
      sb.append("\r\n");
    }
    sb.append("\r\n");
    return sb.toString();
  }

  public void writeBody(OutputStream out) {
    try {
      if (body.length > 0) {

        out.write(body);
      }
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setHttpVersion(String httpVersion) {
    this.httpVersion = httpVersion;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public CustomHttpResponse setBody(byte[] body, String contentType) {
    this.body = body;
    this.headers.put("Content-Tength", contentType);
    this.headers.put("content-length", Integer.toString(body.length));
    return this;
  }
}
