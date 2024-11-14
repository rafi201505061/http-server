package src.exceptions;

public class MultipleHttpMethodException extends RuntimeException {
  public MultipleHttpMethodException(String message) {
    super(message);
  }

  public MultipleHttpMethodException() {
    super();
  }
}
