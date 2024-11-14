package src.repositories;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import src.annotations.Repository;
import src.entities.BlogEntity;

@Repository
public class BlogRepository {
  private ConcurrentMap<String, BlogEntity> blogIdToBlogMap = new ConcurrentHashMap<>();

  public BlogEntity addBlog(BlogEntity entity) {
    blogIdToBlogMap.put(entity.getId(), entity);
    return entity;
  }

  public BlogEntity deleteBlog(String blogId) {
    if (blogIdToBlogMap.containsKey(blogId)) {
      BlogEntity blogEntity = blogIdToBlogMap.get(blogId);
      blogIdToBlogMap.remove(blogId);
      return blogEntity;
    }
    return null;
  }

  public BlogEntity updateBlog(BlogEntity blog) {
    if (blogIdToBlogMap.containsKey(blog.getId())) {
      blogIdToBlogMap.put(blog.getId(), blog);
      return blog;
    }
    return null;
  }

  public BlogEntity getBlog(String blogId) {
    return blogIdToBlogMap.getOrDefault(blogId, null);
  }

  public List<BlogEntity> getAllBlogs() {
    return blogIdToBlogMap.values().stream().collect(Collectors.toList());
  }
}
