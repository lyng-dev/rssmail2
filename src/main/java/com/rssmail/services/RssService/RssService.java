package com.rssmail.services.RssService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.rssmail.models.FeedItem;
import com.rssmail.utils.hashing.HashTree;

import org.springframework.stereotype.Service;



@Service
public class RssService {

  

  public RssService(HashTree hashTree) {
  }

  public ArrayList<FeedItem> getFeed(String feedUrl) throws IllegalArgumentException, FeedException, IOException {
    URL feedSource = new URL(feedUrl);
    SyndFeedInput input = new SyndFeedInput();
    SyndFeed feed = input.build(new XmlReader(feedSource));
    var entries = feed.getEntries();

    return new ArrayList<FeedItem>(entries.stream().map(e -> mapSyndEntryToFeedItem(e)).toList());
  }

  private FeedItem mapSyndEntryToFeedItem(SyndEntry item) {

    final var feedItem = new FeedItem();
    if (item.getUri().length() > 0) feedItem.setUri(item.getUri());
    if (item.getTitle().length() > 0) feedItem.setTitle(item.getTitle());
    if (item.getLink().length() > 0) feedItem.setLink(item.getLink());
    if (item.getPublishedDate() != null) feedItem.setPublishedDate(item.getPublishedDate().toString());

    final var dataBlocks = new ArrayList<String>();
    if (feedItem.getUri().length() > 0) dataBlocks.add(feedItem.getUri());
    if (feedItem.getTitle().length() > 0) dataBlocks.add(feedItem.getTitle());
    if (feedItem.getLink().length() > 0) dataBlocks.add(feedItem.getTitle());
    if (feedItem.getPublishedDate() != null) dataBlocks.add(feedItem.getPublishedDate());

    var tree = HashTree.generateTree(dataBlocks);
    feedItem.setHash(tree.getHash());

    return feedItem;
  }
}
