package src.controllers;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import src.annotations.Autowired;
import src.annotations.Controller;
import src.annotations.Get;
import src.annotations.Path;
import src.annotations.Post;
import src.entities.BlogEntity;
import src.repositories.BlogRepository;
import src.utils.CustomHttpRequest;
import src.utils.CustomHttpResponse;
import src.utils.HttpStatus;

@Controller
@Path("/blogs")
public class BlogController {
  private BlogRepository blogRepository;

  @Autowired
  public BlogController(BlogRepository blogRepository) {
    this.blogRepository = blogRepository;
  }

  @Get
  public CustomHttpResponse getAllBlogs(CustomHttpRequest httpRequest) {
    Gson gson = new Gson();
    String jsonResponse = gson.toJson(this.blogRepository.getAllBlogs());
    return new CustomHttpResponse(HttpStatus.OK).setBody(jsonResponse.getBytes(),
        "application/json");
  }

  @Post
  public CustomHttpResponse createBlog(CustomHttpRequest httpRequest) {
    ObjectMapper objectMapper = new ObjectMapper();
    Gson gson = new Gson();

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
      return new CustomHttpResponse(HttpStatus.CREATED).setBody(jsonResponse.getBytes(),
          "application/json");
    } catch (Exception e) {
      e.printStackTrace();
      return new CustomHttpResponse(HttpStatus.BAD_REQUEST);
    }
  }
}
