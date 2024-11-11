package entities;

import java.util.UUID;

public class BlogEntity {
  private String id;
  private String title;
  private String author;
  // private Date creationTime;
  private String body;

  public BlogEntity() {
    this.id = UUID.randomUUID().toString();

  }

  public BlogEntity(String title, String author, String body) {
    this.id = UUID.randomUUID().toString();
    this.title = title;
    this.author = author;
    this.body = body;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @Override
  public String toString() {
    return "BlogEntity [id=" + id + ", title=" + title + ", author=" + author + ", body=" + body + "]";
  }

}
