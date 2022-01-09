package com.rssmail.services.RssService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;


public class RssService {


  
  public void read() throws IllegalArgumentException, FeedException, IOException {
    URL feedSource = new URL("https://aws.amazon.com/blogs/aws/feed/");
    SyndFeedInput input = new SyndFeedInput();
    SyndFeed feed = input.build(new XmlReader(feedSource));
    System.out.println(feed.toString());
  }
}
