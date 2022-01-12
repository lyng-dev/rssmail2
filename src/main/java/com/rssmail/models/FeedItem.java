package com.rssmail.models;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedItem {

  final private HashMap<String, String> dataBlocks = new HashMap<>();

  private String hash;
  private String uri;
  private String title;
  private String link;
  private String description;
  private String publishedDate;

  public FeedItem() {
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getPublishedDate() {
    return publishedDate;
  }

  public void setPublishedDate(String publishedDate) {
    this.publishedDate = publishedDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String putDataBlock(String keyName, String value) {
    return dataBlocks.put(keyName, value);
  }

  public ArrayList<String> getDataBlocks() {
    return new ArrayList<String>(dataBlocks.entrySet().stream().map(x -> x.getValue()).toList());
  }
  
}
