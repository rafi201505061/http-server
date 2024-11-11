package utils;

public class MyJsonData {
  public String hello;
  public String gello;

  public String getHello() {
    return hello;
  }

  public void setHello(String hello) {
    this.hello = hello;
  }

  public String getGello() {
    return gello;
  }

  public void setGello(String gello) {
    this.gello = gello;
  }

  @Override
  public String toString() {
    return "MyJsonData [hello=" + hello + ", gello=" + gello + "]";
  }
}
