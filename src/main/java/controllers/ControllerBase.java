package controllers;

import utils.CustomHttpRequest;
import utils.CustomHttpResponse;

public interface ControllerBase {
  public CustomHttpResponse handleRequest(CustomHttpRequest httpRequest);
}
