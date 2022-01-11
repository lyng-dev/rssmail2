package com.rssmail.services.RssService;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Service;



@Service
public class RssService {
  public String read(String feedUrl) throws IllegalArgumentException, FeedException, IOException {
    //require that RSS Feed supports pubdate, else throw PubDateRequiredException
    URL feedSource = new URL(feedUrl);
    SyndFeedInput input = new SyndFeedInput();
    SyndFeed feed = input.build(new XmlReader(feedSource));
    var entries = feed.getEntries();


    entries.stream().forEach(x -> System.out.println(String.format("%s - %s - %s",x.getTitle(), x.getLink(), x.getPublishedDate())));
    
    return "";
  }
}
