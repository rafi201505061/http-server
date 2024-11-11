package utils;

public enum ContentType {
  APPLICATION_JSON("application/json"), TEXT_PLAIN("text/plain");

  private String value;

  public String getValue() {
    return value;
  }

  ContentType(String value) {
    this.value = value;
  }

  public static ContentType fromValue(String value) {
    for (ContentType type : ContentType.values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    return null;
  }
}
