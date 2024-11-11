package utils;

public enum HttpStatus {
  OK("200 OK"), BAD_REQUEST("404 Bad Request"), INTERNAL_SERVER_ERROR("500 Internal Server Error"),
  CREATED("201 Created"), NOT_FOUND("404 Not Found"), METHOD_NOT_ALLOWED("405 Method Not Allowed");

  private String value;

  HttpStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
