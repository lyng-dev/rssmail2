package com.rssmail.services.RssService;

import java.io.IOException;
import java.net.URL;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Service;

@Service
public class RssService {
  public String read(String feedUrl) throws IllegalArgumentException, FeedException, IOException {
    URL feedSource = new URL(feedUrl);
    SyndFeedInput input = new SyndFeedInput();
    SyndFeed feed = input.build(new XmlReader(feedSource));
    return feed.toString();
  }
}
