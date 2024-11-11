package controllers;

import utils.CustomHttpRequest;
import utils.CustomHttpResponse;

public interface Controller {
  public CustomHttpResponse handleRequest(CustomHttpRequest httpRequest);
}
