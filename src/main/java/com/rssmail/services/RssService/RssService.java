package com.rssmail.services.RssService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import com.rometools.rome.feed.synd.SyndEntry;
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
 
  public Boolean validateFeed(String feedUrl) {
    try {
      final var feedSource = new URL(feedUrl);
      final var input = new SyndFeedInput();
      final var feed = input.build(new XmlReader(feedSource));
      return true;
    } catch (Exception e) {
      //swallow, because it's not interesting.
    }
    return false;
  }

  //TODO: Implement caching, so multiple subscriptions can use the same response data
  public ArrayList<FeedItem> getFeed(String feedUrl) throws IllegalArgumentException, FeedException, IOException {
    final var feedSource = new URL(feedUrl);
    final var input = new SyndFeedInput();
    final var feed = input.build(new XmlReader(feedSource));
    final var entries = feed.getEntries();

    return new ArrayList<FeedItem>(entries.stream().map(e -> mapSyndEntryToFeedItem(e)).toList());
  }

  private FeedItem mapSyndEntryToFeedItem(SyndEntry item) {

    final var feedItem = new FeedItem();
    if (item.getUri().length() > 0) feedItem.setUri(item.getUri());
    if (item.getTitle().length() > 0) feedItem.setTitle(item.getTitle());
    if (item.getLink().length() > 0) feedItem.setLink(item.getLink());
    if (item.getPublishedDate() != null) feedItem.setPublishedDate(item.getPublishedDate().toString());
    if (item.getDescription() != null) feedItem.setDescription(item.getDescription().getValue().toString());

    final var dataBlocks = new ArrayList<String>();
    if (feedItem.getUri().length() > 0) dataBlocks.add(feedItem.getUri());
    if (feedItem.getTitle().length() > 0) dataBlocks.add(feedItem.getTitle());
    if (feedItem.getLink().length() > 0) dataBlocks.add(feedItem.getTitle());

    final var tree = HashTree.generateTree(dataBlocks);
    feedItem.setHash(tree.getHash());

    return feedItem;
  }
}
