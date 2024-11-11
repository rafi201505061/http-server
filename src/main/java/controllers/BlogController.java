package controllers;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import entities.BlogEntity;
import repositories.BlogRepository;
import utils.CustomHttpRequest;
import utils.CustomHttpResponse;
import utils.HttpMethod;
import utils.HttpStatus;

public class BlogController implements Controller {
  private BlogRepository blogRepository;

  public BlogController(BlogRepository blogRepository) {
    this.blogRepository = blogRepository;
  }

  @Override
  public CustomHttpResponse handleRequest(CustomHttpRequest httpRequest) {
    HttpMethod method = httpRequest.getMethodName();
    Gson gson = new Gson();
    System.out.println(method);
    switch (method) {
      case HttpMethod.GET: {
        String jsonResponse = gson.toJson(this.blogRepository.getAllBlogs());
        return new CustomHttpResponse(HttpStatus.OK).setBody(jsonResponse.getBytes(), "application/json");
      }
      case HttpMethod.POST: {
        System.out.println("matched");
        ObjectMapper objectMapper = new ObjectMapper();

        BlogEntity blogEntity;
        try {
          System.out.println(httpRequest.getBody().toString());
          String utf8String = new String(httpRequest.getBody());

          // Ensure UTF-8 encoding
          byte[] utf8Bytes = utf8String.getBytes(StandardCharsets.UTF_8);
          String utf8EncodedString = new String(utf8Bytes, StandardCharsets.UTF_8);

          blogEntity = objectMapper.readValue(utf8EncodedString, BlogEntity.class);
          BlogEntity newBlogEntity = this.blogRepository.addBlog(blogEntity);
          System.out.println(newBlogEntity);
          String jsonResponse = gson.toJson(newBlogEntity);
          return new CustomHttpResponse(HttpStatus.CREATED).setBody(jsonResponse.getBytes(), "application/json");
        } catch (Exception e) {
          e.printStackTrace();
          return new CustomHttpResponse(HttpStatus.BAD_REQUEST);
        }
      }
      default:
        return new CustomHttpResponse(HttpStatus.METHOD_NOT_ALLOWED);
    }
  }

}
