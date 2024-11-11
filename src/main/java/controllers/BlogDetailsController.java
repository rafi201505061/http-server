package controllers;

import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import entities.BlogEntity;
import repositories.BlogRepository;
import utils.CustomHttpRequest;
import utils.CustomHttpResponse;
import utils.HttpMethod;
import utils.HttpStatus;

public class BlogDetailsController implements Controller {
  private BlogRepository blogRepository;

  public BlogDetailsController(BlogRepository blogRepository) {
    this.blogRepository = blogRepository;
  }

  @Override
  public CustomHttpResponse handleRequest(CustomHttpRequest httpRequest) {
    HttpMethod method = httpRequest.getMethodName();
    Gson gson = new Gson();
    String url = httpRequest.getUrl();
    StringTokenizer st = new StringTokenizer(url.startsWith("/") ? url.substring(1) : url, "/");
    System.out.println(st.nextToken());
    String id = st.nextToken();
    switch (method) {
      case HttpMethod.GET: {
        BlogEntity blogEntity = this.blogRepository.getBlog(id);
        if (blogEntity != null) {
          String jsonResponse = gson.toJson(this.blogRepository.getBlog(id));
          return new CustomHttpResponse(HttpStatus.OK).setBody(jsonResponse.getBytes(), "application/json");
        } else {
          return new CustomHttpResponse(HttpStatus.OK).setBody("Blog Not Found".getBytes(), "text/plain");
        }
      }
      case HttpMethod.PATCH:
      case HttpMethod.PUT: {
        ObjectMapper objectMapper = new ObjectMapper();

        BlogEntity blogEntity;
        try {
          String utf8String = new String(httpRequest.getBody());
          byte[] utf8Bytes = utf8String.getBytes(StandardCharsets.UTF_8);
          String utf8EncodedString = new String(utf8Bytes, StandardCharsets.UTF_8);
          blogEntity = objectMapper.readValue(utf8EncodedString, BlogEntity.class);
          blogEntity.setId(id);
          BlogEntity updatedBlogEntity = this.blogRepository.updateBlog(blogEntity);
          String jsonResponse = gson.toJson(updatedBlogEntity);
          return new CustomHttpResponse(HttpStatus.OK).setBody(jsonResponse.getBytes(), "application/json");
        } catch (Exception e) {
          e.printStackTrace();
          return new CustomHttpResponse(HttpStatus.BAD_REQUEST);
        }
      }
      case HttpMethod.DELETE: {
        this.blogRepository.deleteBlog(id);
        return new CustomHttpResponse(HttpStatus.OK);
      }
      default:
        return new CustomHttpResponse(HttpStatus.METHOD_NOT_ALLOWED);
    }
  }

}
